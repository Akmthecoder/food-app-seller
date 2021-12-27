package com.android.foodieMartSeller.data.request

data class Order(
    var id: String = "",
    var order_id: String = "",
    var order_user: User = User(),
    var feeds: MutableList<OrderFeedItem> = mutableListOf(),
    var delivery_time: String = "",
    var delivery_date: String = "",
    var isPickUp: Boolean = true,
    var status: String = "",
    var deliveryFree: Int = 0
)