package com.android.foodieMartSeller.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.data.request.Order
import com.android.foodieMartSeller.dialog.ViewDialog
import com.android.foodieMartSeller.network.firebase.Connection
import com.android.foodieMartSeller.network.firebase.FirebaseMethods
import com.android.foodieMartSeller.network.firebase.RequestCallback
import com.android.foodieMartSeller.notification.*
import com.android.foodieMartSeller.ui.adapter.OrderSummaryAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_order_summary.*
import kotlinx.android.synthetic.main.app_header.*
import retrofit2.Call

class OrderSummaryActivity : AppCompatActivity() {
    private val TAG = "Order Summary"
    private lateinit var order: Order
    private lateinit var adapter: OrderSummaryAdapter
    private lateinit var apiService: NotificationService
    private lateinit var loadingDialog: ViewDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)
        apiService = Client.getClient()?.create(NotificationService::class.java)!!
        loadingDialog = ViewDialog(this)
        intent?.let {
            val orderStr = it.getStringExtra("Order") as String
            order = Gson().fromJson(orderStr, Order::class.java)
        }

        initiateViews()
        setRecyclerView()
        loadOrderItems()
        mcv_user_info.setOnClickListener {
            val bottomSheet = UserDetailsBottomSheet(order.order_user)
            bottomSheet.show(supportFragmentManager, "Order Sheet")
        }

        btn_cancel.setOnClickListener {
            loadingDialog.showDialog()
            FirebaseDatabase.getInstance().reference.child(Connection.ORDERS).child(order.id)
                .removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        FirebaseMethods.addValueEventChild(
                            "tokens",
                            order.order_user.id,
                            object : RequestCallback {
                                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        val token = dataSnapshot.getValue(String::class.java)
                                        token?.let {
                                            sendNotification(it)
                                        }
                                    }
                                }
                            })
                    } else {
                        loadingDialog.hideDialog()
                    }
                }
        }

        btn_delivered.setOnClickListener {
            order.status = "Delivered"
            val orderStr = Gson().toJson(order)
            FirebaseDatabase.getInstance().reference.child(Connection.ORDERS).child(order.id)
                .setValue(orderStr)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        finish()
                    }
                }
        }
    }

    private fun sendNotification(token: String) {
        val data = Data().apply {
            this.body = "Order Id : ${order.order_id} has been cancelled"
            this.orderId = order.id
            this.purpose = "Order Cancelled"
        }
        val sender = Sender().apply {
            this.data = data
            this.to = token
        }
        apiService.sendNotification(sender)
            .enqueue(object : retrofit2.Callback<Response> {
                override fun onFailure(call: Call<Response>, t: Throwable) {
                    finish()
                    loadingDialog.hideDialog()
                }

                override fun onResponse(
                    call: Call<Response>,
                    response: retrofit2.Response<Response>
                ) {
                    loadingDialog.hideDialog()
                    finish()
                }
            })
    }

    private fun initiateViews() {
        tvHeader.text = TAG
        ivBackHeader.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setRecyclerView() {
        rv_order_items.layoutManager = LinearLayoutManager(this)
        adapter = OrderSummaryAdapter(this)
        rv_order_items.adapter = adapter
    }

    private fun loadOrderItems() {
        adapter.setList(order.feeds)
        user_name.text = order.order_user.username
        user_delivery_date.text="Delivery Date : "+order.delivery_date
        user_delivery_time.text="Delivery Time : "+order.delivery_time
        user_delivery_address.text="Delivery Address : "+order.order_user.address
        var total = 0
        order.feeds.forEach {
            val price = it.feed.feed_types[it.selected_feed_item].price
            total += (it.feedCount * price)
        }
        total += Integer.parseInt(order.deliveryFree.toString())
        tv_total_value.text = "₹ ${total}"
    }
}