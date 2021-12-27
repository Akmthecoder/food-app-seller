package com.android.foodieMartSeller.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.data.request.Feed
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions


class FeedAdapter(val context: Context, var onClick: (Feed) -> Unit) :
    RecyclerView.Adapter<FeedAdapter.Viewholder>() {
    private val categoryFeeds: MutableList<Feed> = mutableListOf()
    private val feeds: MutableList<Feed> = mutableListOf()

    inner class Viewholder(val item: View) : RecyclerView.ViewHolder(item) {
        var feedImage = item.findViewById<AppCompatImageView>(R.id.iv_feed)
        var feedName = item.findViewById<AppCompatTextView>(R.id.tv_feed_name)
        var feedPrize = item.findViewById<AppCompatTextView>(R.id.tv_feed_prize)
        var feedDescription = item.findViewById<AppCompatTextView>(R.id.tv_feed_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_feed, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val feed = categoryFeeds[position]
        if (feed.imageUrl.isNotEmpty()) {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
            Glide.with(context)
                .load(feed.imageUrl)
                .placeholder(R.drawable.ic_logo_new)
                .apply(requestOptions)
                .into(holder.feedImage)
        }

        holder.feedName.text = feed.name
       holder.feedPrize.text = "₹ ${feed.feed_types[0].price}"
        if (feed.description.isNotEmpty()) {
            holder.feedDescription.text = feed.description
        }
        holder.itemView.setOnClickListener {
            onClick.invoke(feed)
        }
    }

    override fun getItemCount(): Int {
        return categoryFeeds.size
    }

    fun setFeedCategory(category: String) {
        categoryFeeds.clear()
        feeds.forEach {
            if (it.category.equals(category)) {
                categoryFeeds.add(it)
            }
        }
        notifyDataSetChanged()
    }

    fun setList(feeds: MutableList<Feed>, category: String) {
        this.feeds.clear()
        this.feeds.addAll(feeds)
        setFeedCategory(category)
    }
}