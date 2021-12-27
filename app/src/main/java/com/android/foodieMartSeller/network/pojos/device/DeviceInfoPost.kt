package com.android.foodieMartSeller.network.pojos.device

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeviceInfoPost(

    @SerializedName("deviceToken")
    var deviceToken: String? = null,

    @SerializedName("securityPatch")
    var securityPatch: String? = null,

    @SerializedName("sdkint")
    var sdkint: String? = null,

    @SerializedName("release")
    var release: String? = null,

    @SerializedName("previewSdkint")
    var previewSdkint: Int? = 0,

    @SerializedName("incremental")
    var incremental: String? = null,

    @SerializedName("codename")
    var codename: String? = null,

    @SerializedName("baseOS")
    var baseOS: String? = null,

    @SerializedName("board")
    var board: String? = null,

    @SerializedName("bootloader")
    var bootloader: String? = null,

    @SerializedName("brand")
    var brand: String? = null,

    @SerializedName("device")
    var device: String? = null,

    @SerializedName("display")
    var display: String? = null,

    @SerializedName("fingerprint")
    var fingerprint: String? = null,

    @SerializedName("hardware")
    var hardware: String? = null,

    @SerializedName("host")
    var host: String? = null,

    @SerializedName("id")
    var id: String? = null,

    @SerializedName("manufacturer")
    var manufacturer: String? = null,

    @SerializedName("model")
    var model: String? = null,

    @SerializedName("product")
    var product: String? = null,

    @SerializedName("supported32BitAbis")
    var supported32BitAbis: List<String>? = null,

    @SerializedName("supported64BitAbis")
    var supported64BitAbis: List<String>? = null,

    @SerializedName("supportedAbis")
    var supportedAbis: List<String>? = null,

    @SerializedName("tags")
    var tags: String? = null,

    @SerializedName("type")
    var type: String? = null,

    @SerializedName("isPhysicalDevice")
    var isPhysicalDevice: Boolean? = false,

    @SerializedName("androidId")
    var androidId: String? = null,

    @SerializedName("systemFeatures")
    var systemFeatures: List<String>? = null

) : Parcelable
