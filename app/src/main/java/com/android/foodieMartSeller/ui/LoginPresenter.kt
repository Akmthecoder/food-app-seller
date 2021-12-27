package com.android.foodieMartSeller.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.android.foodieMartSeller.data.request.Seller
import com.android.foodieMartSeller.dialog.ViewDialog
import com.android.foodieMartSeller.network.firebase.Connection
import com.android.foodieMartSeller.network.firebase.FirebaseMethods
import com.android.foodieMartSeller.network.firebase.RequestCallback
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson


object LoginPresenter {

    fun getCurrentUser(context: Context, currentUser: FirebaseUser, dialog: ViewDialog?) {
        val userId = currentUser.uid
        FirebaseMethods.singleValueEventChild(Connection.SELLERS, userId, object : RequestCallback {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    dialog?.hideDialog()
                    goToHomeAcivity(context)
                } else {
                    FirebaseMethods.singleValueEventChild(
                        Connection.SERVICE,
                        userId,
                        object : RequestCallback {
                            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    dataSnapshot.getValue(Boolean::class.java)?.let {
                                        if (it) {
                                            createUserDatabase(context, userId, currentUser, dialog)
                                        }
                                    }
                                } else {
                                    addToServiceUser(context, currentUser, dialog)
                                }
                            }
                        })
                }
            }
        })
    }

    private fun createUserDatabase(
        context: Context,
        userId: String,
        currentUser: FirebaseUser,
        dialog: ViewDialog?
    ) {
        val user = Seller().apply {
            this.id = userId
            this.phone_number =
                if (currentUser.phoneNumber == null) "" else currentUser.phoneNumber.toString()
        }
        val userStr = Gson().toJson(user)
        FirebaseDatabase.getInstance().reference.child(Connection.SELLERS).child(userId)
            .setValue(userStr).addOnCompleteListener {
                if (it.isSuccessful) {
                    dialog?.hideDialog()
                    goToHomeAcivity(context)
                } else {
                    dialog?.hideDialog()
                }
            }
    }

    private fun addToServiceUser(context: Context, currentUser: FirebaseUser, dialog: ViewDialog?) {
        FirebaseDatabase.getInstance().reference.child(Connection.SERVICE).child(currentUser.uid)
            .setValue(false).addOnCompleteListener {
                if (it.isSuccessful) {
                    createUserDatabase(context, currentUser.uid, currentUser, dialog)
                } else {
                    dialog?.hideDialog()
                }
            }
    }

    fun goToHomeAcivity(context: Context) {
        context.startActivity(Intent(context, SellerHomeActivity::class.java))
        (context as Activity).finish()
    }
}