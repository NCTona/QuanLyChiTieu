package com.example.jetpackcompose.app.features.apiService

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Quản lý việc tải model TFLite từ server và chạy dự đoán chi tiêu.
 *
 * Model nhận đầu vào:
 *  - time_series: [1, WINDOW_SIZE, 1] — chuỗi chi tiêu 4 TUẦN gần nhất (đã normalize)
 *
 * Model trả về:
 *  - output: [1, 1] — giá trị chi tiêu dự đoán tuần tới (đã normalize)
 *
 * KHÔNG CÒN User Embedding — model universal cho mọi user.
 * Cá nhân hóa thông qua dữ liệu input (mỗi user có chuỗi chi tiêu riêng).
 */
object ExpenseForecastHelper {

    private const val TAG = "ForecastHelper"
    private const val MODEL_FILENAME = "expense_model.tflite"
    private const val WINDOW_SIZE = 4  // 4 tuần

    private var interpreter: Interpreter? = null

    /**
     * Tải model mới nhất từ server và lưu vào bộ nhớ trong của app.
     * @return true nếu tải thành công
     */
    suspend fun downloadAndSaveModel(context: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            val apiService = RetrofitProvider.provideApiService(context)
            val response = apiService.downloadModel()

            if (response.isSuccessful && response.body() != null) {
                val modelFile = File(context.filesDir, MODEL_FILENAME)
                response.body()!!.byteStream().use { inputStream ->
                    modelFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                Log.d(TAG, "Model đã tải thành công: ${modelFile.length()} bytes")
                true
            } else {
                Log.e(TAG, "Tải model thất bại: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi tải model: ${e.message}", e)
            false
        }
    }

    /**
     * Kiểm tra model đã tồn tại trong bộ nhớ chưa.
     */
    fun isModelAvailable(context: Context): Boolean {
        return File(context.filesDir, MODEL_FILENAME).exists()
    }

    /**
     * Load model TFLite vào Interpreter.
     */
    fun loadModel(context: Context): Boolean {
        return try {
            val modelFile = File(context.filesDir, MODEL_FILENAME)
            if (!modelFile.exists()) {
                Log.e(TAG, "File model không tồn tại")
                return false
            }

            Log.d(TAG, "Loading model: ${modelFile.absolutePath} (${modelFile.length()} bytes)")

            val bytes = modelFile.readBytes()
            val modelBuffer = ByteBuffer.allocateDirect(bytes.size).order(ByteOrder.nativeOrder())
            modelBuffer.put(bytes)
            modelBuffer.rewind()

            val options = Interpreter.Options()
            options.setUseNNAPI(false)
            
            interpreter = Interpreter(modelBuffer, options)

            // Log chi tiết tensor để debug
            val inputCount = interpreter!!.inputTensorCount
            val outputCount = interpreter!!.outputTensorCount
            Log.d(TAG, "Model loaded OK. Inputs: $inputCount, Outputs: $outputCount")
            for (i in 0 until inputCount) {
                val tensor = interpreter!!.getInputTensor(i)
                Log.d(TAG, "  Input[$i]: name=${tensor.name()}, shape=${tensor.shape().contentToString()}, type=${tensor.dataType()}")
            }
            for (i in 0 until outputCount) {
                val tensor = interpreter!!.getOutputTensor(i)
                Log.d(TAG, "  Output[$i]: name=${tensor.name()}, shape=${tensor.shape().contentToString()}, type=${tensor.dataType()}")
            }

            true
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi load model: ${e.message}", e)
            false
        }
    }

    /**
     * Chạy dự đoán chi tiêu TUẦN tiếp theo.
     *
     * @param recentWeeklyExpenses Danh sách chi tiêu 4 TUẦN gần nhất (giá trị thực, VD: 500000, 1200000,...)
     * @param scaleValue Giá trị dùng để normalize (percentile 95 hoặc giá trị max hợp lý)
     * @return Số tiền dự đoán sẽ chi trong TUẦN tiếp theo, hoặc null nếu lỗi
     */
    fun predict(recentWeeklyExpenses: List<Double>, scaleValue: Double): Double? {

        val interp = interpreter ?: run {
            Log.e(TAG, "Interpreter chưa được load")
            return null
        }

        if (recentWeeklyExpenses.size < WINDOW_SIZE) {
            Log.e(TAG, "Cần ít nhất $WINDOW_SIZE tuần dữ liệu, hiện có ${recentWeeklyExpenses.size}")
            return null
        }

        // Chuẩn bị input ByteBuffer: [1, 4, 1] * 4 bytes/float = 16 bytes
        val inputBuffer = ByteBuffer.allocateDirect(WINDOW_SIZE * 4)
        inputBuffer.order(ByteOrder.nativeOrder())
        
        val last4Weeks = recentWeeklyExpenses.takeLast(WINDOW_SIZE)
        for (amount in last4Weeks) {
            val normalized = if (scaleValue > 0) (amount / scaleValue).toFloat() else 0f
            inputBuffer.putFloat(normalized.coerceIn(0f, 3f))
        }
        inputBuffer.rewind()

        // Chuẩn bị output: mảng 2D [1, 1] để khớp chính xác với shape của model
        val outputBuffer = Array(1) { FloatArray(1) }

        // Chạy inference
        return try {
            interp.run(inputBuffer, outputBuffer)
            val normalizedResult = outputBuffer[0][0]
            val predictedAmount = kotlin.math.abs((normalizedResult * scaleValue).toDouble())
            Log.d(TAG, "Dự đoán tuần tới: $predictedAmount VND (normalized: $normalizedResult) [Input: $last4Weeks, Scale: $scaleValue]")
            predictedAmount
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi inference: ${e.message}", e)
            null
        }
    }

    /**
     * Giải phóng tài nguyên Interpreter.
     */
    fun close() {
        interpreter?.close()
        interpreter = null
    }
}
