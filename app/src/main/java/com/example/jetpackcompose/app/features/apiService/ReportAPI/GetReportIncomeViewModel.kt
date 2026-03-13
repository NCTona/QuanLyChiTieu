package com.example.jetpackcompose.app.features.apiService.ReportAPI

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.app.features.apiService.ReportIncomeResponse
import com.example.jetpackcompose.app.features.apiService.RetrofitProvider
import kotlinx.coroutines.launch

class GetReportIncomeViewModel(private val context: Context) : ViewModel() {

    private val api = RetrofitProvider.provideApiService(context)

    var reportData: ReportIncomeResponse? = null
        private set

    var reportStatus: String = ""
        private set

    fun getIncomeReport(
        month: Int,
        year: Int,
        onSuccess: (ReportIncomeResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = api.getReportIncome(month, year)
                Log.d("GetReportViewModel", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    val reportResponse = response.body()
                    if (reportResponse != null) {
                        reportData = reportResponse
                        reportStatus = "Report fetched successfully"
                        onSuccess(reportResponse)
                    } else {
                        reportStatus = "Error: Empty report response from server"
                        onError(reportStatus)
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    reportStatus = "Error fetching report: $errorBodyString"
                    onError(reportStatus)
                }
            } catch (e: Exception) {
                reportStatus = "Error: ${e.localizedMessage}"
                Log.e("GetReportViewModel", "Error fetching report: ${e.localizedMessage}", e)
                onError(reportStatus)
            }
        }
    }
}
