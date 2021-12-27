package com.android.foodieMartSeller.data.models.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationResponse(

    @SerializedName("data")
    var `data`: List<Data?>? = null,

    @SerializedName("msg")
    var msg: String? = null
) : Parcelable {
    @Parcelize
    data class Data(

        @SerializedName("data")
        var `data`: String? = null,

        @SerializedName("time")
        var time: String? = null
    ) : Parcelable
}