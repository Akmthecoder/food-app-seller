package com.android.foodieMartSeller.network.pojos

import com.google.gson.annotations.SerializedName

class ErrorResponse {
    @SerializedName("msg")
    val message: String? = null

    @SerializedName("data")
    val data: String? = null

}