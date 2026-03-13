package com.example.jetpackcompose.app.features.apiService.RefreshAccessTokenAPI

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.jetpackcompose.app.features.apiService.RetrofitProvider

class RefreshTokenWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val apiService = RetrofitProvider.provideApiService()
        val executor = RefreshTokenExecutor(applicationContext, apiService)

        executor.refresh()
        return Result.success()
    }

}
