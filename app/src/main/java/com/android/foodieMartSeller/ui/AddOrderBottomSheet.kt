package com.android.foodieMartSeller.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.data.request.Feed
import com.android.foodieMartSeller.dialog.ViewDialog
import com.android.foodieMartSeller.network.firebase.Connection
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.main.layout_order_bottom_sheet.*


class AddOrderBottomSheet(var activity: Activity, private val feed: Feed) :
    BottomSheetDialogFragment(),
    View.OnClickListener {
    private lateinit var loadingDialog: ViewDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_order_bottom_sheet, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = ViewDialog(requireContext())
        setBottomSheetView()
        onClick()
    }


    private fun onClick() {
        btn_delivered.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

    private fun setBottomSheetView() {
        if (feed.imageUrl.isNotEmpty()) {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(46))
            Glide.with(this)
                .load(feed.imageUrl).apply(requestOptions).into(iv_feed)
        }
        tv_feed_name.text = feed.name
        tv_feed_price.text = "₹ ${feed.feed_types[0].price}"
        tv_feed_description.text = feed.description
        btn_toggle.isChecked = feed.isAvailable
        tiEtStock.setText("${feed.total_stock_size}")
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_cancel -> {
                val mDialog = MaterialDialog.Builder(activity)
                    .setTitle("Delete")
                    .setMessage("Are you sure to delete?")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Yes"
                    ) { dialogInterface, which ->
                        deleteFeed()
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton("No") { dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    .build()
                mDialog.show()
            }
            R.id.btn_delivered -> {
                if (checkStockValidation()) {
                    loadingDialog.showDialog()
                    feed.isAvailable = btn_toggle.isChecked
                    feed.total_stock_size = Integer.parseInt(tiEtStock.text.toString())
                    val feedStr = Gson().toJson(feed)
                    FirebaseDatabase.getInstance().reference.child(Connection.FEED).child(feed.id)
                        .setValue(feedStr).addOnCompleteListener {
                            if (it.isSuccessful) {
                                loadingDialog.hideDialog()
                                dismiss()
                            } else {
                                loadingDialog.hideDialog()
                                Toast.makeText(
                                    activity,
                                    "Some thing went wrong.Please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                }
            }
        }
    }

    private fun checkStockValidation(): Boolean {
        if (tiEtStock.text.toString() == "") {
            Toast.makeText(activity, "Stock Item is mandatory", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun deleteFeed() {
        loadingDialog.showDialog()
        FirebaseDatabase.getInstance().reference.child(Connection.FEED).child(feed.id).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    loadingDialog.hideDialog()
                    dismiss()
                } else {
                    loadingDialog.hideDialog()
                    Toast.makeText(
                        activity,
                        "Some thing went wrong.Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}