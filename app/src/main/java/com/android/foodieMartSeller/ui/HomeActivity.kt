package com.android.foodieMartSeller.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.data.request.Feed
import com.android.foodieMartSeller.dialog.ViewDialog
import com.android.foodieMartSeller.network.firebase.Connection
import com.android.foodieMartSeller.network.firebase.FirebaseMethods
import com.android.foodieMartSeller.network.firebase.RequestCallback
import com.android.foodieMartSeller.ui.adapter.FeedAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_header.*

class HomeActivity() : AppCompatActivity() {
    lateinit var adapter: FeedAdapter
    lateinit var viewDialog:ViewDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        viewDialog= ViewDialog(this)
        initializeViews()
        setUpRecyclerView()
        loadFeed()
    }

    private fun initializeViews() {
        ivBackHeader.setImageResource(R.drawable.ic_back_arrow)
        tvHeader.text = "Edit Items"
        ivBackHeader.setOnClickListener {
            onBackPressed()
        }
        tabMode?.let {
            it.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        val category = it.text.toString()
                        adapter.setFeedCategory(category)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        }
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        rv_feed.layoutManager = layoutManager
        adapter = FeedAdapter(this,::openBottomSheetUtil)
        rv_feed.adapter = adapter
    }
    private fun openBottomSheetUtil(feed: Feed) {
        val bottomSheet = AddOrderBottomSheet(this, feed)
        bottomSheet.show(supportFragmentManager, "Order Sheet")
    }

    private fun loadFeed() {
        viewDialog.showDialog()
        FirebaseMethods.addValueEvent(Connection.FEED, object : RequestCallback {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val feeds = mutableListOf<Feed>()
                val category = mutableListOf<String>()
                dataSnapshot.children.forEach {
                    val feedStr = it.getValue(String::class.java)
                    val feed = Gson().fromJson(feedStr, Feed::class.java)
                    if (!category.contains(feed.category)) {
                        category.add(feed.category)
                    }
                    feed?.let { feeds.add(feed) }
                }
                if (category.isNotEmpty()) {
                    addCategoryTabs(category)
                }
                tabMode?.let {
                    val category = it.getTabAt(it.selectedTabPosition)?.text.toString()
                    adapter.setList(feeds, category)
                    viewDialog.hideDialog()
                }
            }
        })
    }
    private fun addCategoryTabs(category: List<String>) {
        tabMode?.let {
            it.removeAllTabs()
            category.forEach { it1 ->
                val tab = it.newTab().setText(it1)
                it.addTab(tab)
            }
        }
    }
}