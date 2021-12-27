package com.android.foodieMartSeller.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.data.request.DeliveryPrice
import com.android.foodieMartSeller.dialog.ViewDialog
import com.android.foodieMartSeller.network.firebase.Connection
import com.android.foodieMartSeller.utils.CommonUtils
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_delivery_amount.*
import kotlinx.android.synthetic.main.activity_delivery_amount.bt_proceed
import kotlinx.android.synthetic.main.activity_delivery_amount.root
import kotlinx.android.synthetic.main.app_header.*

class DeliveryPriceActivity : AppCompatActivity() {
    private lateinit var viewDialog: ViewDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_amount)
        viewDialog = ViewDialog(this)
        initializeViews()
        ivBackHeader.setOnClickListener {
            onBackPressed()
        }
        bt_proceed.setOnClickListener {
            if (tiEtPriceNew.text.toString()!="" && tiEtPriceNew1.text.toString()!=""){
                viewDialog.showDialog()
                val deliveryPrice= DeliveryPrice(tiEtPriceNew.text.toString().toInt(),tiEtPriceNew1.text.toString().toInt())
                val gson= Gson().toJson(deliveryPrice)
                FirebaseDatabase.getInstance().reference.child(Connection.DELIVERY_RANGE).setValue(gson).addOnCompleteListener {
                    if (it.isSuccessful) {
                        viewDialog.hideDialog()
                        CommonUtils.showSnackBar(this, "Price update successfully.", root)

                    } else {
                        viewDialog.hideDialog()
                        CommonUtils.showSnackBar(this, "Unable to Update Price.Please try again", root)
                    }
                }
            }else{
                Toast.makeText(this,"Delivery Price is mandatory.",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun initializeViews() {
        ivBackHeader.setImageResource(R.drawable.ic_back_arrow)
        tvHeader.text = "Delivery Price"
    }
}