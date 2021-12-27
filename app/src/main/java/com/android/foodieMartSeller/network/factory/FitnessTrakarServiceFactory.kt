package com.android.foodieMartSeller.network.factory

import android.app.Application
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.android.foodieMartSeller.network.interceptor.AuthorizationInterceptor
import com.google.gson.Gson
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class FitnessTrakarServiceFactory(val application: Application) {

    fun create(): Retrofit {
        val client = createOkHttp()
        return Retrofit.Builder()
            .baseUrl("")
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
    }

    private fun createOkHttp(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .dns(Dns.SYSTEM)
            .connectTimeout(OK_HTTP_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(OK_HTTP_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(AuthorizationInterceptor(application))


            builder.addNetworkInterceptor(StethoInterceptor())
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)


        return builder.build()
    }

    companion object {
        private const val OK_HTTP_TIMEOUT = 60L
    }
}
