package com.android.foodieMartSeller.data.request

data class OrderFeedItem(
    var feed: Feed = Feed(),
    var feedCount: Int = 0,
    var selected_feed_item: Int = -1
)
