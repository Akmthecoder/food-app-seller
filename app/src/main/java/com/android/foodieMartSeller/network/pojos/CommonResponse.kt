package com.android.foodieMartSeller.network.pojos

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommonResponse(

    @SerializedName("data")
    var data: String? = null,

    @SerializedName("msg")
    var msg: String? = null

) : Parcelable
