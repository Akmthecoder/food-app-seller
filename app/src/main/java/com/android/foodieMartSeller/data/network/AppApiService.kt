package com.android.foodieMartSeller.data.network

import com.android.foodieMartSeller.data.models.response.NotificationResponse
import io.reactivex.Single
import retrofit2.http.GET

interface AppApiService {

    @GET("v1/notification/fetch/list")
    fun getNotifications(): Single<NotificationResponse>

}