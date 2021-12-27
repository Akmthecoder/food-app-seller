package com.android.foodieMartSeller.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.data.request.User
import com.android.foodieMartSeller.dialog.ViewDialog
import com.android.foodieMartSeller.network.firebase.Connection
import com.android.foodieMartSeller.utils.CommonUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        if(FirebaseAuth.getInstance().currentUser != null){
            startActivity(Intent(this,LoginActivity::class.java))
        }
        tvSignUpDetails.makeLinks(
            Pair("Sign In", View.OnClickListener {
                onBackPressed()
            })
        )

        bt_proceed.setOnClickListener {
            doSignUp()
        }
    }

    private fun doSignUp() {
        if (doValidations()) {
            ViewDialog(this).showDialog()
            val username = tiEtUserName.text.toString()
            val email = tiEtEmail.text.toString()
            val phoneNumber = etPhoneNo.text.toString()
            val password = tiEtPassword.text.toString()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val database =
                            FirebaseDatabase.getInstance().reference.child(Connection.USERS)
                        val id = FirebaseAuth.getInstance().currentUser?.uid.toString()
                        val user = User().apply {
                            this.id = id
                            this.email = email
                            this.phone_number = phoneNumber
                            this.username = username
                            this.isSeller=true
                        }
                        val userJsonStr = Gson().toJson(user)
                        //load the data
                        database.child(id).setValue(userJsonStr).addOnCompleteListener {
                            if (it.isSuccessful) {
                                //move to verification screen
                                startActivity(Intent(this, LoginActivity::class.java))
                            } else {
                                ViewDialog(this).hideDialog()
                                CommonUtils.showSnackBar(this,"Sign Up Failed",root)
                            }
                        }
                    } else {
                        ViewDialog(this).hideDialog()
                        CommonUtils.showSnackBar(this,"Sign Up Failed",root)
                    }
                }
        }
    }

    private fun dismissSignUp() {

    }

    private fun startSignUp() {

    }

    private fun doValidations(): Boolean {
        return true
    }

    private fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        var startIndexOfLink = -1
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun updateDrawState(textPaint: TextPaint) {
                    textPaint.color = textPaint.linkColor
                    textPaint.isUnderlineText = false
                }

                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance()
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }
}