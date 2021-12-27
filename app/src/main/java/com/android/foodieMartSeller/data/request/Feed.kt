package com.android.foodieMartSeller.data.request

data class Feed(
    var id: String = "",
    var name: String = "",
    var isAvailable: Boolean = true,
    var description: String = "",
    var category: String = "",
    var imageUrl: String = "",
    var maxItems: Int = 0,
    var feed_types: MutableList<FeedType> = mutableListOf(),
    var total_stock_size: Int = 0
)