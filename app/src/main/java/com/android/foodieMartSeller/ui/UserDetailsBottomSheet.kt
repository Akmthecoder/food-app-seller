package com.android.foodieMartSeller.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.data.request.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_user_bottom_sheet.*


class UserDetailsBottomSheet(private val user: User) : BottomSheetDialogFragment(),
    View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_user_bottom_sheet, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetView()
        onClick()
    }

    private fun onClick() {

    }

    private fun setBottomSheetView() {
        user_name.text = user.username
        user_email.text = user.email
        user_phoneno.text = user.phone_number
       // user_delivery_address.setText()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

        }
    }
}