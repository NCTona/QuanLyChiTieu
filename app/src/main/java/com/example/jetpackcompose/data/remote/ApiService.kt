package com.example.jetpackcompose.data.remote

import com.example.jetpackcompose.data.remote.dto.AISummaryResponse
import com.example.jetpackcompose.data.remote.dto.AnomalyResponse
import com.example.jetpackcompose.data.remote.dto.ApiResponse
import com.example.jetpackcompose.data.remote.dto.CategoryForecastResponse
import com.example.jetpackcompose.data.remote.dto.FindTransactionResponse
import com.example.jetpackcompose.data.remote.dto.FixedTransaction
import com.example.jetpackcompose.data.remote.dto.FixedTransactionUpdate
import com.example.jetpackcompose.data.remote.dto.GetFixedTransactionResponse
import com.example.jetpackcompose.data.remote.dto.LimitTransaction
import com.example.jetpackcompose.data.remote.dto.LoginData
import com.example.jetpackcompose.data.remote.dto.LoginResponse
import com.example.jetpackcompose.data.remote.dto.OTPResponse
import com.example.jetpackcompose.data.remote.dto.PostTransactionResponse
import com.example.jetpackcompose.data.remote.dto.RefreshToken
import com.example.jetpackcompose.data.remote.dto.RegistrationData
import com.example.jetpackcompose.data.remote.dto.RegistrationResponse
import com.example.jetpackcompose.data.remote.dto.RemainLimit
import com.example.jetpackcompose.data.remote.dto.ReportExpenseResponse
import com.example.jetpackcompose.data.remote.dto.ReportIncomeResponse
import com.example.jetpackcompose.data.remote.dto.ResetPassword
import com.example.jetpackcompose.data.remote.dto.SendOtp
import com.example.jetpackcompose.data.remote.dto.SpendingAlertResponse
import com.example.jetpackcompose.data.remote.dto.Transaction
import com.example.jetpackcompose.data.remote.dto.TransactionResponse
import com.example.jetpackcompose.data.remote.dto.TrendResponse
import com.example.jetpackcompose.data.remote.dto.VerifyOtp
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

object BaseURL {
    const val baseUrl = "https://jay-humorous-koi.ngrok-free.app"
}

interface ApiService {

    // ===== Auth =====
    @POST("/api/users/register")
    suspend fun register(@Body registrationData: RegistrationData): Response<RegistrationResponse>

    @POST("/api/users/login")
    suspend fun login(@Body LoginData: LoginData): Response<LoginResponse>

    @POST("/api/users/logout")
    suspend fun logout(): Response<ApiResponse>

    @POST("/api/users/refresh-token")
    suspend fun refreshToken(@Body refreshToken: RefreshToken): Response<LoginResponse>

    @POST("/api/users/verify-otp")
    suspend fun verifyOtp(@Body verifyOtp: VerifyOtp): Response<OTPResponse>

    @POST("/api/users/send-otp")
    suspend fun sendOtp(@Body sendOtp: SendOtp): Response<ApiResponse>

    @POST("/api/users/reset-password")
    suspend fun resetPassword(@Body resetPassword: ResetPassword): Response<ApiResponse>

    // ===== Transactions =====
    @GET("api/finance")
    suspend fun getTransactions(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<TransactionResponse>

    @POST("api/transactions")
    suspend fun postTransaction(
        @Body transaction: Transaction
    ): Response<PostTransactionResponse>

    @PUT("/api/transactions/{transactionId}")
    suspend fun putTransaction(
        @Path("transactionId") transactionId: Int,
        @Body transaction: Transaction
    ): Response<TransactionResponse>

    @DELETE("/api/transactions/{transactionId}")
    suspend fun deleteTransaction(
        @Path("transactionId") transactionId: Int,
    ): Response<TransactionResponse>

    @GET("/api/transactions/search")
    suspend fun findTransactions(
        @Query("note") note: String,
        @Query("categoryName") categoryName: String,
        @Query("amount") amount: Long?,
    ): Response<List<FindTransactionResponse>>

    // ===== Fixed Transactions =====
    @POST("/api/fixed-transactions")
    suspend fun addFixedTransaction(
        @Body fixedTransaction: FixedTransaction
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

    // ===== Budget =====
    @PUT("/api/category-limits/save")
    suspend fun addLimitTransaction(
        @Body limitTransaction: List<LimitTransaction.CategoryLimit>
    ): Response<ApiResponse>

    @GET("/api/category-limits/remaining")
    suspend fun getLimitTransaction(): Response<List<RemainLimit.CategoryLimit>>

    @GET("/api/category-limits/current")
    suspend fun getBudgetCategory(): Response<List<RemainLimit.CategoryLimit>>

    // ===== Reports =====
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

    // ===== AI / Forecast =====
    @Streaming
    @GET("/api/internal/model/download")
    suspend fun downloadModel(): Response<ResponseBody>

    @GET("/api/forecast/categories")
    suspend fun getCategoryForecasts(): Response<List<CategoryForecastResponse>>

    @GET("/api/forecast/trend/{categoryId}")
    suspend fun getCategoryTrend(
        @Path("categoryId") categoryId: Long
    ): Response<TrendResponse>

    @GET("api/forecast/anomalies")
    suspend fun getAnomalies(
        @Header("Authorization") token: String
    ): Response<List<AnomalyResponse>>

    @GET("api/forecast/alerts")
    suspend fun getSpendingAlerts(
        @Header("Authorization") token: String
    ): Response<List<SpendingAlertResponse>>

    @GET("api/forecast/summary")
    suspend fun getAISummary(): Response<AISummaryResponse>
}
