package com.example.jetpackcompose.app.features.apiService.RefreshAccessTokenAPI

import org.json.JSONObject

object JwtUtils {

    fun getExpireTimeMillis(token: String): Long {
        val json = decodePayload(token) ?: return 0
        val expSeconds = json.optLong("exp", 0)
        return expSeconds * 1000
    }

    fun getUserId(token: String): Int? {
        val json = decodePayload(token) ?: return null
        // Cập nhật: Server trả về Key là "userId" chứ không phải "id"
        return if (json.has("userId")) json.getInt("userId") else null
    }

    private fun decodePayload(token: String): JSONObject? {
        try {
            val parts = token.split(".")
            if (parts.size < 2) return null

            val payload = String(
                android.util.Base64.decode(
                    parts[1],
                    android.util.Base64.URL_SAFE
                )
            )
            return JSONObject(payload)
        } catch (e: Exception) {
            return null
        }
    }
}
