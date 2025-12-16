package com.levelup.data.remote

import com.levelup.data.session.SessionManager
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor(private val sessionManager: SessionManager) :
        Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { sessionManager.authTokenFlow.first() }

        android.util.Log.d("AuthInterceptor", "=== REQUEST DEBUG ===")
        android.util.Log.d("AuthInterceptor", "URL: ${originalRequest.url}")
        android.util.Log.d("AuthInterceptor", "Method: ${originalRequest.method}")

        val request = originalRequest.newBuilder()
        if (!token.isNullOrEmpty()) {
            val cleanToken = token.trim()
            val hexStart =
                    cleanToken.take(10).map { String.format("%02X", it.toByte()) }.joinToString(" ")

            android.util.Log.d(
                    "AuthInterceptor",
                    "Token found: ${cleanToken.take(20)}... (Length: ${cleanToken.length})"
            )
            android.util.Log.d("AuthInterceptor", "Token HEX start: $hexStart")

            request.header("Authorization", "Bearer $cleanToken")
        } else {
            android.util.Log.e("AuthInterceptor", "⚠️ Token is NULL or EMPTY!")
        }

        val response = chain.proceed(request.build())
        android.util.Log.d("AuthInterceptor", "Response code: ${response.code}")
        if (response.code == 401) {
            android.util.Log.e(
                    "AuthInterceptor",
                    "❌ 401 UNAUTHORIZED - Token might be invalid or expired"
            )
        }
        return response
    }
}
