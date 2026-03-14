package com.example.jetpackcompose.app.features.apiService

import com.example.jetpackcompose.app.screens.LimitTransaction
import com.example.jetpackcompose.app.screens.RemainLimit
import com.example.jetpackcompose.app.screens.Transaction
import com.example.jetpackcompose.app.features.apiService.FixedTransactionAPI.FixedTransaction
import com.example.jetpackcompose.app.features.apiService.FixedTransactionAPI.FixedTransactionUpdate
import com.example.jetpackcompose.app.features.apiService.FixedTransactionAPI.GetFixedTransactionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.ResponseBody
import retrofit2.http.Streaming


object BaseURL {
    val baseUrl = "https://jay-humorous-koi.ngrok-free.app"
}

data class RegistrationData(
    val phone_number: String,
    val email: String,
    val password: String,
    val retype_password: String
)

data class LoginData(
    val phone_number: String,
    val password: String,
)

data class LoginResponse(
    val status: String,
    val message: String,  // Giả sử API trả về một trường 'token'
    val accessToken: String,
    val refreshToken: String
)

data class RegistrationResponse(
    val status: String,
    val message: String
)

data class  SendOtp(
    val email: String
)

data class RefreshToken(
    val refreshToken: String
)

data class VerifyOtp(
    val email: String,
    val otp: String
)

data class ResetPassword(
    val email: String,
    val resetToken: String,
    val newPassword: String,
    val confirmPassword: String
)


data class FindTransactionResponse(
    val categoryName: String,
    val amount: Long,
    val transactionDate: List<Int>,
    val note: String?,
    val type: String,
    val transaction_id: Long
)

data class PostTransactionResponse(
    val categoryName: String,
    val amount: Double,
    val transactionDate: List<Int>,
    val note: String,
    val type: String,
    val transaction_id: Long
)

data class ApiResponse(
    val status: String,
    val message: String
)

data class OTPRespone(
    val status: String,
    val message: String,
    val resetToken: String
)

data class ReportExpenseResponse(
    val totalIncome: Long,
    val totalExpense: Long,
    val netAmount: Long,
    val categoryExpenseReports: List<CategoryExpenseReport>
){
    data class CategoryExpenseReport(
        val categoryId: Int,
        val categoryName: String,
        val spentAmount: Long,
        val percentSpent: Double,
        val percentLimit: Double
    )
}

data class ReportIncomeResponse(
    val totalIncome: Long,
    val totalExpense: Long,
    val netAmount: Long,
    val categoryIncomeReports: List<CategoryIncomeReport>
){
    data class CategoryIncomeReport(
        val categoryId: Int,
        val categoryName: String,
        val categoryIncome: Long,
        val percentIncome: Double
    )
}

data class TransactionResponse(
    val dailyTransactions: Map<String, DailyTransaction>,
    val totalIncome: Long,
    val totalExpense: Long,
    val balance: Long,
    val transactions: List<TransactionDetail>
) {

    data class DailyTransaction(
        val totalDailyIncome: Long,
        val totalDailyExpense: Long
    )

    data class TransactionDetail(
        val categoryName: String,
        val amount: Long,
        val transactionDate: List<Int>,
        val note: String?,
        val type: String?,
        val transaction_id: Int
    )
}

interface ApiService {

    // API cho đăng ký
    @POST("/api/users/register")
    suspend fun register(@Body registrationData: RegistrationData): Response<RegistrationResponse>

    /// API cho đăng nhập
    @POST("/api/users/login")
    suspend fun login(@Body LoginData: LoginData): Response<LoginResponse>

    /// API cho đăng xuất
    @POST("/api/users/logout")
    suspend fun logout(): Response<ApiResponse>

    /// Refresh token
    @POST("/api/users/refresh-token")
    suspend fun refreshToken(@Body refreshToken: RefreshToken): Response<LoginResponse>

    @POST("/api/users/verify-otp")
    suspend fun verifyOtp(@Body verifyOtp: VerifyOtp): Response<OTPRespone>

    @POST("/api/users/send-otp")
    suspend fun sendOtp(@Body sendOtp: SendOtp): Response<ApiResponse>

    @POST("/api/users/reset-password")
    suspend fun resetPassword(@Body resetPassword: ResetPassword): Response<ApiResponse>


    // API cho giao dịch
    @GET("api/finance")
    suspend fun getTransactions(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<TransactionResponse>

    //API cho nhập transaction
    @POST("api/transactions")
    suspend fun postTransaction(
        @Body transaction: Transaction
    ): Response<PostTransactionResponse>


    // API cho Fixed
    @POST("/api/fixed-transactions")
    suspend fun addFixedTransaction(
        @Body fixedTransaction: FixedTransaction // Dùng FixedTransaction mà không phân biệt loại giao dịch
    ): Response<ApiResponse>

    @GET("/api/fixed-transactions")
    suspend fun getFixedTransactions(): Response<GetFixedTransactionResponse>

    @PUT("/api/fixed-transactions/{fixedTransactionId}")
    suspend fun putFixedTransaction(
        @Path("fixedTransactionId") fixedTransactionId: Int,
        @Body fixedTransaction: FixedTransactionUpdate
    ): Response<ApiResponse>

    @DELETE("/api/fixed-transactions/{fixedTransactionId}")
    suspend fun deleteFixedTransaction(
        @Path("fixedTransactionId") fixedTransactionId: Int
    ): Response<GetFixedTransactionResponse>

    //API cho PutLimit
    @PUT("/api/category-limits/save")
    suspend fun addLimitTransaction(
        @Body limitTransaction: List<LimitTransaction.CategoryLimit>
    ): Response<ApiResponse>

    @GET("/api/category-limits/remaining")
    suspend fun getLimitTransaction(): Response<List<RemainLimit.CategoryLimit>>

    @GET("/api/category-limits/current")
    suspend fun getBudgetCategory(): Response<List<RemainLimit.CategoryLimit>>

    @PUT("/api/transactions/{transactionId}")
    suspend fun putTransaction(
        @Path("transactionId") transactionId: Int,  // Tham số này sẽ thay thế {transactionId} trong URL
        @Body transaction: Transaction
    ): Response<TransactionResponse>

    @DELETE("/api/transactions/{transactionId}")
    suspend fun deleteTransaction(
        @Path("transactionId") transactionId: Int,  // Tham số này sẽ thay thế {transactionId} trong URL
    ): Response<TransactionResponse>

    @GET("/api/report/monthly_expense")
    suspend fun getReportExpense(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<ReportExpenseResponse>

    @GET("/api/report/monthly_income")
    suspend fun getReportIncome(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<ReportIncomeResponse>

    @GET("/api/transactions/search")
    suspend fun findTransactions(
        @Query("note") note: String,
        @Query("categoryName") categoryName: String,
        @Query("amount") amount: Long?,
    ): Response<List<FindTransactionResponse>>

    // API tải model AI dự đoán chi tiêu
    @Streaming
    @GET("/api/internal/model/download")
    suspend fun downloadModel(): Response<ResponseBody>

    // API dự đoán chi tiêu theo danh mục (LightGBM server-side)
    @GET("/api/forecast/categories")
    suspend fun getCategoryForecasts(
        @Query("normalized") normalized: Boolean? = null
    ): Response<List<CategoryForecastResponse>>

    @GET("/api/forecast/trend/{categoryId}")
    suspend fun getCategoryTrend(
        @Path("categoryId") categoryId: Long
    ): Response<TrendResponse>

    // ===== Anomaly Detection =====
    @GET("api/forecast/anomalies")
    suspend fun getAnomalies(
        @Header("Authorization") token: String
    ): Response<List<AnomalyResponse>>

    // ===== Spending Pattern Alerts =====
    @GET("api/forecast/alerts")
    suspend fun getSpendingAlerts(
        @Header("Authorization") token: String
    ): Response<List<SpendingAlertResponse>>

    // ===== AI Summary (tổng hợp 3 model) =====
    @GET("api/forecast/summary")
    suspend fun getAISummary(): Response<AISummaryResponse>

}

// ===== Data classes cho Category Forecast (LightGBM) =====

data class CategoryForecastResponse(
    val category_id: Long,
    val predicted_spending: Double,
    val current_spending: Double,
    val trend: String,
    val change_percent: Double,
    val budget_limit: Double,
    val is_essential: Boolean,
    val recommended_daily_allocation: Double,
    val reason: String
)

data class TrendResponse(
    val category_id: Long,
    val population_average: Double,
    val user_spending: Double,
    val deviation_percent: Double,
    val status: String,
    val message: String
)

// ===== Data classes cho Anomaly Detection (Isolation Forest) =====

data class AnomalyResponse(
    val transaction_id: Long,
    val amount: Double,
    val category_name: String,
    val is_anomaly: Boolean,
    val anomaly_score: Double,
    val message: String
)

// ===== Data classes cho Spending Pattern Alert (Thống kê) =====

data class SpendingAlertResponse(
    val alert_date: String,
    val day_of_month: Int,
    val expected_spending: Double,
    val times_higher: Double,
    val category_name: String,
    val message: String,
    val suggestion: String
)

// ===== Data class cho AI Summary (Tổng hợp insights) =====

data class AISummaryResponse(
    // Dữ liệu đầu vào cho TFLite (Android)
    val weekly_forecast: WeeklyForecastData?,
    val category_forecasts: List<CategoryForecastResponse>,
    val anomaly_count: Int,
    val top_anomalies: List<AnomalyResponse>,
    val upcoming_alerts: List<SpendingAlertResponse>
)

data class WeeklyForecastData(
    val predicted_spending: Double,
    val input_weeks: List<Double>,
    val trend: String,
    val change_percent: Double,
    val remaining_days: Int,
    val is_over_budget: Boolean,
    val warning_message: String
)
