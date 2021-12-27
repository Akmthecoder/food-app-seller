package com.android.foodieMartSeller.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class FirebaseIdInstanceService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()

        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth?.let {
            val firebaseUser = it.currentUser
            firebaseUser?.let {
                val token: String? = FirebaseInstanceId.getInstance().token
                token?.let {
                    updateToken(it, firebaseUser)
                }
            }
        }
    }

    private fun updateToken(token: String, firebaseUser: FirebaseUser) {
        val reference = FirebaseDatabase.getInstance().getReference("tokens")
        reference.child(firebaseUser.uid).setValue(token)
    }
}