package com.android.foodieMartSeller.network.pojos.device

import android.graphics.drawable.Drawable

data class App(
    val icon: Drawable?,
    val name: String,
    val packageName: String,
)