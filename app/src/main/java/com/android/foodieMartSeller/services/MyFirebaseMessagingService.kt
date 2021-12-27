package com.android.foodieMartSeller.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.data.request.Order
import com.android.foodieMartSeller.network.firebase.Connection
import com.android.foodieMartSeller.network.firebase.FirebaseMethods
import com.android.foodieMartSeller.network.firebase.RequestCallback
import com.android.foodieMartSeller.ui.SellerHomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val CHANNEL_ID = "com.bake.bakeryapp"
    private val CHANNEL_NAME = "Nabin Bakery"

    private lateinit var notificationManager: NotificationManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {
            sendNotifcation(remoteMessage)
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        FirebaseDatabase.getInstance().reference.child("tokens")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).setValue(p0)
    }

    private fun sendNotifcation(remoteMessage: RemoteMessage) {
        var orderId = remoteMessage.data.get("orderId")
        val body = remoteMessage.data.get("body")
        val purpose = remoteMessage.data.get("purpose")

        val j = Integer.parseInt(orderId?.replace("\\D".toRegex(), ""))
        val intent = Intent(this, SellerHomeActivity::class.java)
        intent.putExtra("purpose", "myOrders")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)

        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var imageBitmap = BitmapFactory.decodeResource(this.resources, R.drawable.ic_logo_new)
        FirebaseMethods.singleValueEventChild(
            Connection.ORDERS, orderId.toString(),
            object : RequestCallback {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    val orderStr = dataSnapshot.getValue(String::class.java)
                    val order = Gson().fromJson(orderStr, Order::class.java)
                    order.let {
                        //Build version is equals to oreo or greater
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            val notificationChannel = NotificationChannel(
                                CHANNEL_ID,
                                CHANNEL_NAME,
                                NotificationManager.IMPORTANCE_DEFAULT
                            )
                            notificationChannel.enableLights(false)
                            notificationChannel.enableVibration(true)
                            notificationChannel.lockscreenVisibility =
                                Notification.VISIBILITY_PRIVATE

                            notificationManager.createNotificationChannel(
                                notificationChannel
                            )
                            val defaultSound =
                                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                            val builder =
                                Notification.Builder(applicationContext, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_logo_new)
                                    .setContentText(body)
                                    .setContentTitle(purpose)
                                    .setLargeIcon(imageBitmap)
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)

                            var i = 0
                            if (j > 0) {
                                i = j
                            }
                            notificationManager.notify(i, builder.build())

                        } else {

                            val defaultSound =
                                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                            val builder =
                                NotificationCompat.Builder(this@MyFirebaseMessagingService)
                                    .setSmallIcon(R.drawable.ic_logo_new)
                                    .setContentText(body)
                                    .setContentTitle(purpose)
                                    .setLargeIcon(imageBitmap)
                                    .setAutoCancel(true)
                                    .setSound(defaultSound)
                                    .setContentIntent(pendingIntent)

                            var i = 0
                            if (j > 0) {
                                i = j
                            }
                            notificationManager.notify(i, builder.build())
                        }
                    }
                }
            })
    }
}
