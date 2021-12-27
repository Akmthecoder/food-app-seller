package com.android.foodieMartSeller.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.dialog.ViewDialog
import com.android.foodieMartSeller.utils.CommonUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.app_header.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private lateinit var loadingDialog: ViewDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        tvHeader.text = "Login"
        ivBackHeader.visibility = View.GONE
        loadingDialog = ViewDialog(this)

        bt_proceed.setOnClickListener {
            doLogin()
        }
    }

    private fun doLogin() {
        if (doValidations()) {
            loadingDialog.showDialog()

            val phoneNum = "+91" + tiEtEmail.text.toString()

            val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                .setPhoneNumber(phoneNum)
                .setTimeout(30L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    override fun onCodeSent(
                        verificationId: String,
                        forceResendingToken: PhoneAuthProvider.ForceResendingToken
                    ) {
                        loadingDialog.hideDialog()
                        Log.d("OTP", "onCodeSent" + verificationId)
                        val intent = Intent(this@LoginActivity, OtpActivity::class.java)
                        intent.putExtra("verificationId", verificationId)
                        startActivity(intent)
                        finish()
                    }

                    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                        loadingDialog.hideDialog()
                        Log.d("OTP", "phoneAuthCredential" + phoneAuthCredential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        loadingDialog.hideDialog()
                        Log.d("OTP", "FirebaseException" + e)
                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private fun doGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("816149521447-nm9r0nr61rhsrk09mj6uf79t0o3ct8lu.apps.googleusercontent.com")
            .requestEmail().build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.result
                loadingDialog.showDialog()
                firebaseGoogleSign(account)
            } catch (e: Exception) {
                print("")
            }
        }
    }

    private fun firebaseGoogleSign(account: GoogleSignInAccount) {
        account?.let {
            val credential = GoogleAuthProvider.getCredential(it.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener {
                }
        }
    }

    private fun doValidations(): Boolean {
        if (tiEtEmail.text.toString() == "") {
            CommonUtils.showSnackBar(this, "Phone Number is Mandatory", root)
            return false
        } else if (tiEtEmail.text?.trim()?.length!! < 10) {
            CommonUtils.showSnackBar(this, "Phone Number must be 10 digit.", root)
            return false
        }
        return true
    }
}