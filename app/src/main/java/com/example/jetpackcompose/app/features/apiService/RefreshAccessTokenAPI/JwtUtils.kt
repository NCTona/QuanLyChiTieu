package com.example.jetpackcompose.app.features.apiService.RefreshAccessTokenAPI

import org.json.JSONObject

object JwtUtils {

    fun getExpireTimeMillis(token: String): Long {
        val parts = token.split(".")
        if (parts.size < 2) return 0

        val payload = String(
            android.util.Base64.decode(
                parts[1],
                android.util.Base64.URL_SAFE
            )
        )

        val json = JSONObject(payload)
        val expSeconds = json.getLong("exp")

        return expSeconds * 1000
    }
}
