package com.example.jetpackcompose.app.features.apiService.RefreshAccessTokenAPI

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.jetpackcompose.app.features.apiService.TokenStorage
import java.util.concurrent.TimeUnit

object RefreshTokenScheduler {

    private const val SAFE_MARGIN_MINUTES = 2L

    fun schedule(context: Context) {
        val tokenStorage = TokenStorage(context)
        val accessToken = tokenStorage.getAccessToken() ?: return

        val expireTime = JwtUtils.getExpireTimeMillis(accessToken)
        var delay = expireTime - System.currentTimeMillis() -
                SAFE_MARGIN_MINUTES * 60 * 1000

        if (delay <= 0) delay = 0

        val request = OneTimeWorkRequestBuilder<RefreshTokenWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "refresh_token_work",
                ExistingWorkPolicy.REPLACE,
                request
            )
    }

}

