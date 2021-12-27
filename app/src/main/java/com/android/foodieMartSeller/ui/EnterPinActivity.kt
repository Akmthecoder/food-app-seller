package com.android.foodieMartSeller.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.dialog.ViewDialog
import com.android.foodieMartSeller.network.firebase.Connection
import com.android.foodieMartSeller.network.firebase.FirebaseMethods
import com.android.foodieMartSeller.network.firebase.RequestCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.enter_4_digit_mpin.*

class EnterPinActivity : AppCompatActivity() {
    private var finalPinValue = ""
    var pinValue = -1
    lateinit var viewDialog: ViewDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_entry)
        viewDialog = ViewDialog(this)
        setInitialAlpha()
        val fArray = arrayOfNulls<InputFilter>(1)
        fArray[0] = LengthFilter(4)
        setEventListenersOnPinPadButtons(findViewById(R.id.quickButtons))
        btnNum11.setOnClickListener(
            PinPadClickListener(
                -1
            )
        )
        this.window.statusBarColor = ContextCompat.getColor(this, R.color.gray1A1919)
    }

    private fun createPIN() {
        viewDialog.showDialog()
        FirebaseMethods.singleValueEvent("MPIN", object : RequestCallback {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                viewDialog.hideDialog()
                pinValue = dataSnapshot.getValue(Int::class.java) as Int
                if (pinValue == finalPinValue.toInt()) {
                    finish()
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        FirebaseInstanceId.getInstance().token.let {
                            FirebaseDatabase.getInstance().reference.child(Connection.TOKENS)
                                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                .setValue(it).addOnCompleteListener {
                                    if(it.isSuccessful){
                                        startActivity(Intent(this@EnterPinActivity, SellerHomeActivity::class.java))
                                    }
                                }
                        }
                    } else {
                        startActivity(Intent(this@EnterPinActivity, LoginActivity::class.java))
                    }
                } else {
                    Toast.makeText(
                        this@EnterPinActivity,
                        "Please enter the Correct PIN.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }


    private fun setInitialAlpha() {
        first_dot.alpha = 0.78f
        second_dot.alpha = 0.78f
        third_dot.alpha = 0.78f
        fourth_dot.alpha = 0.78f
    }

    private fun setEventListenersOnPinPadButtons(containerView: LinearLayout?) {
        if (containerView != null) {
            val children = containerView.childCount
            var i = 0
            while (i < children) {
                val childView = containerView.getChildAt(i)
                if (childView is LinearLayout) {
                    // For row layouts
                    setEventListenersOnPinPadButtons(childView)
                } else if (childView is Button) {
                    // For Digits 0 to 9
                    childView.setOnClickListener(PinPadClickListener(Integer.valueOf(childView.text.toString())))
                }
                i += 1
            }
        }
    }

    private fun setAlphas(pinLength: Int) {
        enableMPINItemCircle(first_dot, pinLength >= 1)
        enableMPINItemCircle(second_dot, pinLength >= 2)
        enableMPINItemCircle(third_dot, pinLength >= 3)
        enableMPINItemCircle(fourth_dot, pinLength >= 4)
        if (pinLength == 4) {
            createPIN()
        }
    }

    private fun enableMPINItemCircle(item: ImageView, enable: Boolean) {
        if (enable) {
            item.setImageResource(R.drawable.ic_mpin_dot_enabled)
        } else {
            item.setImageResource(R.drawable.ic_mpin_dot_disabled)
        }
    }

    inner class PinPadClickListener(keyValue: Int) : View.OnClickListener {
        private var keyValue = 0
        override fun onClick(v: View) {
            var currentValue: String = finalPinValue
            if (keyValue in 0..9) {
                if (finalPinValue.length <= 4) {
                    currentValue += keyValue.toString()
                    finalPinValue = currentValue
                    setAlphas(finalPinValue.length)
                }
            } else {
                if (currentValue.isNotEmpty()) {
                    currentValue = currentValue.substring(0, currentValue.length - 1)
                    finalPinValue = currentValue
                    setAlphas(finalPinValue.length)
                }
            }
        }

        init {
            this.keyValue = keyValue
        }
    }
}