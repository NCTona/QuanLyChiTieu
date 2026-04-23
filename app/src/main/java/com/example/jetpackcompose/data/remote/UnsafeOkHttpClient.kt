package com.example.jetpackcompose.data.remote

import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

object UnsafeOkHttpClient {

    private data class TrustConfig(
        val sslSocketFactory: SSLSocketFactory,
        val trustManager: X509TrustManager
    )

    private fun createTrustConfig(): TrustConfig {
        val trustManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
        return TrustConfig(sslContext.socketFactory, trustManager)
    }

    fun create(): OkHttpClient = createCleanClient()

    fun createCleanClient(): OkHttpClient {
        val config = createTrustConfig()
        return OkHttpClient.Builder()
            .sslSocketFactory(config.sslSocketFactory, config.trustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    fun createClientWithAuth(
        authInterceptor: okhttp3.Interceptor,
        tokenAuthenticator: okhttp3.Authenticator
    ): OkHttpClient {
        val config = createTrustConfig()
        return OkHttpClient.Builder()
            .sslSocketFactory(config.sslSocketFactory, config.trustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }
}
