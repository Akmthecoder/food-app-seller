package com.android.foodieMartSeller.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.data.request.OrderFeedItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class OrderSummaryAdapter(private val context: Context) :
    RecyclerView.Adapter<OrderSummaryAdapter.Viewholder>() {
    val feeds: MutableList<OrderFeedItem> = mutableListOf()

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var feedImage = itemView.findViewById<AppCompatImageView>(R.id.iv_feed)
        val feedName = itemView.findViewById<AppCompatTextView>(R.id.tv_feed_name)
        val feedPrize = itemView.findViewById<AppCompatTextView>(R.id.tv_feed_prize)
        val itemAmount = itemView.findViewById<AppCompatTextView>(R.id.tv_quantity_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_cart_item, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val orderItem = feeds[position]
        if (orderItem.feed.imageUrl.isNotEmpty()) {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
            Glide.with(context).load(orderItem.feed.imageUrl).apply(requestOptions)
                .into(holder.feedImage)
        }
        holder.feedName.text = orderItem.feed.name
        holder.feedPrize.text =
            "₹ ${orderItem.feed.feed_types[orderItem.selected_feed_item].price * orderItem.feedCount}"
        holder.itemAmount.text = "x${orderItem.feedCount}"
    }

    override fun getItemCount(): Int {
        return feeds.size
    }

    fun setList(feeds: MutableList<OrderFeedItem>) {
        this.feeds.clear()
        this.feeds.addAll(feeds)
        notifyDataSetChanged()
    }
}