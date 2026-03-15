package com.example.jetpackcompose.presentation.report

import com.example.jetpackcompose.data.remote.ApiService
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.remote.dto.ReportExpenseResponse
import kotlinx.coroutines.launch

@HiltViewModel
class GetReportExpenseViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

    var reportData: ReportExpenseResponse? = null
        private set

    var reportStatus: String = ""
        private set

    fun getExpenseReport(
        month: Int,
        year: Int,
        onSuccess: (ReportExpenseResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = apiService.getReportExpense(month, year)
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
