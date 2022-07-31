package com.bikcodeh.dogrecognizer.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

object ApiServiceInterceptor: Interceptor {

    const val NEEDS_AUTH_HEADER_KEY = "needs_authentication"
    private var sessionToken: String? = null

    fun setToken(token: String) {
        sessionToken = token
    }

    fun clearToken() {
        sessionToken = null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        if (request.header(NEEDS_AUTH_HEADER_KEY) != null) {
            if (sessionToken == null) {
                throw RuntimeException("Need to be authenticated to perform this action")
            } else {
                requestBuilder.addHeader("AUTH-TOKEN", sessionToken!!)
            }
        }
        return chain.proceed(requestBuilder.build())
    }
}