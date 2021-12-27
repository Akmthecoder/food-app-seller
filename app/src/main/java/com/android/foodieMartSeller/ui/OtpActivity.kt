package com.android.foodieMartSeller.ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.dialog.ViewDialog
import com.android.foodieMartSeller.utils.CommonUtils
import com.android.foodieMartSeller.utils.CommonUtils.makeLinks
import com.android.foodieMartSeller.utils.GenericTextWatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.app_header.*


class OtpActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var otpEditArray: Array<EditText>
    private lateinit var verificationId: String
    private lateinit var viewDialog: ViewDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        viewDialog = ViewDialog(this)
        intent?.let {
            verificationId = it.getStringExtra("verificationId") as String
        }
        loadTextWatcher()
        setOnCLickLister()
        tvOtpDetails.makeLinks(
            Pair("Resend Code", View.OnClickListener {
                Toast.makeText(applicationContext, "Sign Up", Toast.LENGTH_SHORT).show()
            })
        )
    }

    private fun setOnCLickLister() {
        tvOtpDetails.setOnClickListener(this)
        ivBackHeader.setOnClickListener(this)
        bt_proceed.setOnClickListener(this)
    }

    private fun loadTextWatcher() {
        otpEditArray =
            arrayOf<EditText>(
                otp_edit_box1,
                otp_edit_box2,
                otp_edit_box3,
                otp_edit_box4,
                otp_edit_box5,
                otp_edit_box6
            )

        otp_edit_box1.addTextChangedListener(GenericTextWatcher(otp_edit_box1, otpEditArray))
        otp_edit_box2.addTextChangedListener(GenericTextWatcher(otp_edit_box2, otpEditArray))
        otp_edit_box3.addTextChangedListener(GenericTextWatcher(otp_edit_box3, otpEditArray))
        otp_edit_box4.addTextChangedListener(GenericTextWatcher(otp_edit_box4, otpEditArray))
        otp_edit_box5.addTextChangedListener(GenericTextWatcher(otp_edit_box5, otpEditArray))
        otp_edit_box6.addTextChangedListener(GenericTextWatcher(otp_edit_box6, otpEditArray))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackHeader -> {
                onBackPressed()
            }
            R.id.bt_proceed -> {
                if (otpEditArray[0].text.toString() != "" && otpEditArray[1].text.toString() != "" && otpEditArray[2].text.toString() != "" && otpEditArray[3].text.toString() != "" && otpEditArray[4].text.toString() != "" && otpEditArray[5].text.toString() != "") {
                    viewDialog.showDialog()
                    val otpToSend =
                        otpEditArray[0].text.toString() +
                                otpEditArray[1].text.toString() +
                                otpEditArray[2].text.toString() +
                                otpEditArray[3].text.toString() +
                                otpEditArray[4].text.toString() +
                                otpEditArray[5].text.toString()
                    Toast.makeText(this, otpToSend, Toast.LENGTH_SHORT).show()
                    signInUser(otpToSend)
                } else {
                    CommonUtils.showSnackBar(this, "OTP Should be 6 digit code", root_otp_layout)
                }
            }
        }
    }

    private fun signInUser(otpToSend: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otpToSend)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.user?.let {
                        LoginPresenter.getCurrentUser(this, it, viewDialog)
                    }
                } else {
                    Toast.makeText(
                        this,
                        it.exception?.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    viewDialog.hideDialog()
                }
            }
    }
}