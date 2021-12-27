package com.android.foodieMartSeller.data.request

data class User(
    var id: String = "",
    var username: String = "",
    var email: String = "",
    var phone_number: String = "",
    var isSeller: Boolean = true,
    var address: String = ""
)
