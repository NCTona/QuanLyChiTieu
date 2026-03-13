package com.example.jetpackcompose.app.features.apiService.RefreshAccessTokenAPI

import kotlinx.coroutines.sync.Mutex

object TokenRefreshLock {
    val mutex = Mutex()
}
