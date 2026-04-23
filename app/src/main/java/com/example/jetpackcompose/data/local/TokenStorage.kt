package com.example.jetpackcompose.data.local

import com.example.jetpackcompose.data.remote.dto.RefreshToken
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.jetpackcompose.presentation.auth.JwtUtils

class TokenStorage(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            "secure_user_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun saveTokens(access: String, refresh: String) {
        prefs.edit()
            .putString("is_logged_in", true.toString())
            .putString("auth_token", access)
            .putString("refresh_token", refresh)
            .apply()
    }

    fun getAccessToken(): String? =
        prefs.getString("auth_token", null)

    fun getRefreshToken(): String? =
        prefs.getString("refresh_token", null)

    fun clear() {
        prefs.edit().clear().apply()
    }

    // Hàm mới – lấy refresh token
    fun getLogInStatus(): String? {
        return prefs.getString("is_logged_in", null)
    }

    fun isLoggedIn(): Boolean {
        return getLogInStatus() == "true"
    }

    fun isTokenCleared(): Boolean {
        return getAccessToken() == null
    }

    fun clearAccessTokenExpired(context: Context) {
        val accessToken = getAccessToken() ?: return

        val expireTime = JwtUtils.getExpireTimeMillis(accessToken)

        val delay = expireTime - System.currentTimeMillis()

        if (delay <= 0) prefs.edit().remove("auth_token").apply()

    }

    fun isAccessTokenExpired(context: Context): Boolean {
        val accessToken = getAccessToken() ?: return true

        val expireTime = JwtUtils.getExpireTimeMillis(accessToken)

        val delay = expireTime - System.currentTimeMillis()

        if (delay <= 0) return true

        return false
    }

    fun isRefreshTokenExpired(context: Context): Boolean {
        val refreshToken = getRefreshToken() ?: return false

        val expireTime = JwtUtils.getExpireTimeMillis(refreshToken)

        val delay = expireTime - System.currentTimeMillis()

        if (delay <= 0) return true

        return false

    }
}
