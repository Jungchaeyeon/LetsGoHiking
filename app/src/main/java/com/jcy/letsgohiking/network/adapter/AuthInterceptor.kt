package com.jcy.letsgohiking.network.adapter

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()

        return chain.proceed(
            originalRequest.newBuilder().apply {
//                if (repositoryCached.getId().isNotEmpty()) {
//                    header("id", repositoryCached.getId())
//                }
                url(url)
                method(originalRequest.method, originalRequest.body)
            }.build()
        )
    }
}