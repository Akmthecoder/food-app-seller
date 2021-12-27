package com.android.foodieMartSeller.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.data.request.Order
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions


class OrderAdapter(val context: Context, var openOrderSummary: (Order) -> Unit) :
    RecyclerView.Adapter<OrderAdapter.Viewholder>() {
    private val orders: MutableList<Order> = mutableListOf()

    inner class Viewholder(val item: View) : RecyclerView.ViewHolder(item) {
        val feedImage = item.findViewById<AppCompatImageView>(R.id.iv_feed)
        val deliveryDate = item.findViewById<AppCompatTextView>(R.id.tv_delivery_date)
        var feedName = item.findViewById<AppCompatTextView>(R.id.tv_user_name)
        var feedPrize = item.findViewById<AppCompatTextView>(R.id.tv_feed_prize)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_orders, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val order = orders[position]
        val user = orders[position].order_user
        var totalPrice = 0
        order.feeds.forEach {
            val price = it.feed.feed_types[it.selected_feed_item].price
            val currentAmount = price * it.feedCount
            totalPrice += currentAmount
        }
        if(order.deliveryFree.equals("Free") == false){
            totalPrice+=order.deliveryFree
        }
        holder.feedName.text = user.username
        holder.deliveryDate.text = order.delivery_date+" "+order.delivery_time
        holder.feedPrize.text = "₹ ${totalPrice}"
        if (order.feeds[0].feed.imageUrl.isNotEmpty()) {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
            Glide.with(context).load(order.feeds[0].feed.imageUrl)
                .apply(requestOptions).into(holder.feedImage)
        }

        holder.itemView.setOnClickListener {
            openOrderSummary(orders[position])
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun setList(orders: MutableList<Order>) {
        this.orders.clear()
        this.orders.addAll(orders)
        notifyDataSetChanged()
    }
}