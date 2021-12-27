package com.android.foodieMartSeller.network.firebase

import com.google.firebase.database.DataSnapshot

interface RequestCallback {
    fun onDataChanged(dataSnapshot: DataSnapshot)
}