package com.android.foodieMartSeller.network.firebase

import com.google.firebase.database.*

object FirebaseMethods {
    lateinit var databaseReference: DatabaseReference

    fun addValueEvent(reference: String, requestCallback: RequestCallback) {
        databaseReference = FirebaseDatabase.getInstance().getReference(reference)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                requestCallback.onDataChanged(p0)
            }
        })
    }

    fun singleValueEvent(reference: String, requestCallback: RequestCallback) {
        databaseReference = FirebaseDatabase.getInstance().getReference(reference)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                requestCallback.onDataChanged(p0)
            }
        })
    }

    fun singleValueEventChild(reference: String, child: String, requestCallback: RequestCallback) {
        databaseReference = FirebaseDatabase.getInstance().getReference(reference).child(child)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                requestCallback.onDataChanged(p0)
            }
        })
    }

    fun singleValueEventDifferentChild(
        reference: String,
        childReference: String,
        requestCallback: RequestCallback
    ) {
        databaseReference = FirebaseDatabase.getInstance().getReference(reference).child(
            childReference
        )
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                requestCallback.onDataChanged(p0)
            }
        })
    }

    fun addValueEventDifferentChild(
        reference: String,
        childReference: String,
        requestCallback: RequestCallback
    ) {
        databaseReference = FirebaseDatabase.getInstance().getReference(reference).child(
            childReference
        )
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                requestCallback.onDataChanged(p0)
            }
        })
    }

    fun addValueEventChild(reference: String, child: String, requestCallback: RequestCallback) {
        databaseReference = FirebaseDatabase.getInstance().getReference(reference).child(
            child
        )
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                requestCallback.onDataChanged(p0)
            }
        })
    }
}