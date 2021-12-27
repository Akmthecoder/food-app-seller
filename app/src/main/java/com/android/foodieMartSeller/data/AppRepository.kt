package com.android.foodieMartSeller.data

import com.android.foodieMartSeller.data.models.response.NotificationResponse
import com.android.foodieMartSeller.data.network.AppApiService
import io.reactivex.Single
import javax.inject.Inject

class AppRepository @Inject constructor(private val apiService: AppApiService) {

    fun getNotifications(): Single<NotificationResponse> {
        return apiService.getNotifications()
    }
}