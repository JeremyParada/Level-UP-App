package com.levelup.data.remote

import com.levelup.data.session.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            sessionManager.authTokenFlow.first()
        }
        val request = chain.request().newBuilder()
        if (!token.isNullOrEmpty()) {
            android.util.Log.d("AuthInterceptor", "Adding Authorization header: Bearer ${token.take(10)}...")
            request.addHeader("Authorization", "Bearer $token")
        } else {
            android.util.Log.e("AuthInterceptor", "Token is NULL or EMPTY!")
        }
        return chain.proceed(request.build())
    }
}
