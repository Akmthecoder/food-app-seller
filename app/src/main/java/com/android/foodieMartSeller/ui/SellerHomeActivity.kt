package com.android.foodieMartSeller.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.data.request.Order
import com.android.foodieMartSeller.network.firebase.Connection
import com.android.foodieMartSeller.network.firebase.FirebaseMethods
import com.android.foodieMartSeller.network.firebase.RequestCallback
import com.android.foodieMartSeller.ui.adapter.OrderAdapter
import com.google.firebase.database.DataSnapshot
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_seller_home.*
import kotlinx.android.synthetic.main.app_header.*

class SellerHomeActivity : AppCompatActivity() {
    lateinit var adapter: OrderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_home)

        ivBackHeader.visibility = View.GONE
        ivSetting.visibility = View.VISIBLE
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddItemCategory::class.java))
        }
        initializeViews()
        setUpRecyclerView()
        loadOrders()
        ivSetting.setOnClickListener {
            startActivity(Intent(this,SellerSettingActivity::class.java))
        }
    }

    private fun initializeViews() {
        ivBackHeader.setImageResource(R.drawable.ic_account)
        tvHeader.text = "Orders"
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        rv_order.layoutManager = layoutManager
        adapter = OrderAdapter(this, ::openOrderSummary)
        rv_order.adapter = adapter
    }

    private fun openOrderSummary(order: Order) {
        val orderStr = Gson().toJson(order)
        val intent = Intent(this, OrderSummaryActivity::class.java)
        intent.putExtra("Order", orderStr)
        startActivity(intent)
    }

    fun loadOrders() {
        FirebaseMethods.addValueEvent(Connection.ORDERS, object : RequestCallback {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val orderList = mutableListOf<Order>()
                var count = 0
                dataSnapshot.children.forEach {
                    val orderStr = it.getValue(String::class.java)
                    val order = Gson().fromJson(orderStr, Order::class.java)
                    if (order.status.equals("Delivered") == false) {
                        orderList.add(order)
                        if (++count == 5) {
                            count = 0
                            adapter.setList(orderList)
                        }
                    }
                }
                adapter.setList(orderList)
            }
        })
    }
}