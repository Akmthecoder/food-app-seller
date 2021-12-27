package com.android.foodieMartSeller.network.interceptor

import android.app.Application
import com.android.foodieMartSeller.network.NetworkConstant
import com.android.foodieMartSeller.utils.SharedPrefUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthorizationInterceptor(val application: Application) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request: Request = chain.request()
        val newRequestBuilder = request.newBuilder()

        if (isLoginServices(request).not()) {
            newRequestBuilder.addHeader(
                NetworkConstant.HEADER_AUTHORIZATION,
                SharedPrefUtil(application).authKey
            )
        }
        return chain.proceed(newRequestBuilder.build())
    }

    private fun isLoginServices(request: Request): Boolean {
        return request.url()
            .toString().contains("/login") || request.url().toString()
            .contains("/signup/social")
    }
}
