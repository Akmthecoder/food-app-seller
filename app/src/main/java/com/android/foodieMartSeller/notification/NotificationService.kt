package com.android.foodieMartSeller.notification

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationService {

    @Headers(
        "Content-Type: application/json",
        "Authorization: key=AAAAH3Sh9Sk:APA91bEPSIF2l1RB-oKYUDdNhUeskyFLAd1QP_xKs3BdVvUfO8SxOoP8Iekxnn29cAV0qy-qvqtdsl-lYfGVJnIRz2UGmz-85WFZPCa2ORy2oDefDZZQ5dGNfkb-g4uxMttRbjnjzJBe"
    )

    @POST("fcm/send")
    fun sendNotification(@Body body: Sender): Call<Response>
}