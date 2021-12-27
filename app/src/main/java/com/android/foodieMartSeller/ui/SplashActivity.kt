package com.android.foodieMartSeller.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.data.request.Seller
import com.android.foodieMartSeller.network.firebase.Connection
import com.android.foodieMartSeller.network.firebase.FirebaseMethods
import com.android.foodieMartSeller.network.firebase.RequestCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_splash)
        Handler().postDelayed({
            if (FirebaseAuth.getInstance().currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                FirebaseInstanceId.getInstance().token?.let {
                    FirebaseDatabase.getInstance().reference.child(Connection.TOKENS)
                        .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).setValue(it)
                }
                FirebaseMethods.singleValueEventChild(
                    Connection.SELLERS,
                    FirebaseAuth.getInstance().currentUser?.uid.toString(),
                    object : RequestCallback {
                        override fun onDataChanged(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.value == null) {
                                FirebaseAuth.getInstance().signOut()
                                startActivity(
                                    Intent(
                                        this@SplashActivity,
                                        LoginActivity::class.java
                                    )
                                )
                            } else {

                                val userStr = dataSnapshot.getValue(String::class.java)
                                val userJson = Gson().fromJson(userStr, Seller::class.java)
                                startActivity(
                                    Intent(
                                        this@SplashActivity,
                                        SellerHomeActivity::class.java
                                    )
                                )
                                finish()
                            }
                        }
                    })
            }
        }, 1000)
    }
}