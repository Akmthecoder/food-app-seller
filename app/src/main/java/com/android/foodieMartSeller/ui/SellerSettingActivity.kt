package com.android.foodieMartSeller.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.dialog.ViewDialog
import com.android.foodieMartSeller.network.firebase.Connection
import com.android.foodieMartSeller.network.firebase.FirebaseMethods
import com.android.foodieMartSeller.network.firebase.RequestCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.main.activity_seller_setting.*
import kotlinx.android.synthetic.main.app_header.*


class SellerSettingActivity : AppCompatActivity() {
    private lateinit var loadingDialog: ViewDialog
    private var shopStatus: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_setting)
        loadingDialog = ViewDialog(this)
        ivBackHeader.visibility = View.VISIBLE
        ivSetting.visibility = View.GONE
        initializeViews()
        setShopStatus()
        shop_edit_feed.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        cl_delivery_charge.setOnClickListener {
            startActivity(Intent(this, DeliveryPriceActivity::class.java))
        }
        shop_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (shopStatus != isChecked) {
                loadingDialog.showDialog()
                FirebaseDatabase.getInstance().reference.child(Connection.SHOP_OPEN_STATUS)
                    .setValue(isChecked).addOnCompleteListener {
                        shopStatus = isChecked
                        if (isChecked) {
                            shop_config.text = "Shop Open"
                        } else {
                            shop_config.text = "Shop Close"
                        }
                        loadingDialog.hideDialog()
                    }
            }
        }
        ivBackHeader.setOnClickListener {
            onBackPressed()
        }

        shop_logout.setOnClickListener {
            val mDialog = MaterialDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { dialogInterface, which ->
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, EnterPinActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    dialogInterface.dismiss()
                    finish()
                }
                .setNegativeButton("No"){dialogInterface, which ->
                    dialogInterface.dismiss()
                }
                .build()
            mDialog.show()
        }
    }

    private fun setShopStatus() {
        loadingDialog.showDialog()
        FirebaseMethods.singleValueEvent(Connection.SHOP_OPEN_STATUS, object : RequestCallback {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(Boolean::class.java)?.let {
                    shopStatus = it
                    if (shopStatus) {
                        shop_config.text = "Shop Open"
                    } else {
                        shop_config.text = "Shop Close"
                    }
                    shop_switch.isChecked = shopStatus
                    loadingDialog.hideDialog()
                }
            }
        })
    }
    private fun initializeViews() {
        ivBackHeader.setImageResource(R.drawable.ic_back_arrow)
        tvHeader.text = "Setting"
    }
}