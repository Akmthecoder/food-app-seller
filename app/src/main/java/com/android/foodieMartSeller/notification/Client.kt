package com.android.foodieMartSeller.notification

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Client {
    private val BASE_URL = "https://fcm.googleapis.com/"
    private var retrofit: Retrofit? = null
    fun getClient(): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }
}