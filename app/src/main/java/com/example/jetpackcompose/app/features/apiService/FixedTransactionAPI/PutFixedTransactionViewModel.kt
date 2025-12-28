package com.example.jetpackcompose.app.features.apiService.FixedTransactionAPI

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.jetpackcompose.app.features.apiService.ApiService
import com.example.jetpackcompose.app.features.apiService.BaseURL
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PutFixedTransactionViewModel(private val context: Context) : ViewModel() {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_user_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private val gson = GsonBuilder().setLenient().create()
    private val api = Retrofit.Builder()
        .baseUrl(BaseURL.baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(ApiService::class.java)

    var fixedTransactionStatus: String = ""
        private set

    private fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun putFixedTransaction(
        fixed_transaction_id: Int,
        data: FixedTransactionUpdate,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = getToken()

        if (token.isNullOrEmpty()) {
            fixedTransactionStatus = "Lỗi: Không tìm thấy token. Vui lòng đăng nhập lại."
            onError(fixedTransactionStatus)
            return
        }

        viewModelScope.launch {
            try {
                Log.d("PutFixedTransactionViewModel", "Token: $token")
                Log.d("PutFixedTransactionViewModel", "Fixed Transaction ID: $fixed_transaction_id")
                Log.d("PutFixedTransactionViewModel", "Fixed Transaction Data: $data")

                val response = api.putFixedTransaction("Bearer $token", fixed_transaction_id, data)
                Log.d("PutFixedTransactionViewModel", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    fixedTransactionStatus = "Giao dịch cố định đã được cập nhật thành công"
                    onSuccess(fixedTransactionStatus)
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    fixedTransactionStatus = "Lỗi cập nhật giao dịch cố định: $errorBodyString"
                    onError(fixedTransactionStatus)
                }
            } catch (e: Exception) {
                fixedTransactionStatus = "Lỗi: ${e.message}"
                onError(fixedTransactionStatus)
            }
        }
    }
}
