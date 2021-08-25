package com.example.scoreup.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.scoreup.R
import com.example.scoreup.firestore.FirestoreClass
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        tv_li_register.setOnClickListener(this)
        btn_li_forgot_password.setOnClickListener(this)
        btn_li_submit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.tv_li_register -> startActivity(Intent(this, RegisterActivity::class.java))
                R.id.btn_li_forgot_password -> startActivity(
                    Intent(
                        this,
                        ForgotPasswordActivity::class.java
                    )
                )
                R.id.btn_li_submit -> logInSubmitPressed()
            }
        }
    }


    private fun logInSubmitPressed() {
        if (validateLogInEntries()) {
            showProgressDialog(resources.getString(R.string.please_wait_text))

            val emailId = et_li_email_id.text.toString().trim { it <= ' ' }
            val password = et_li_password.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailId, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        FirestoreClass().getUserDetails(this)

                    } else {
                        hideProgressDialog()
                        showSnackBar(true, it.exception!!.message.toString())
                    }
                }
        }
    }

    private fun validateLogInEntries(): Boolean {
        return when {
            TextUtils.isEmpty(et_li_email_id.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(true, resources.getString(R.string.error_email_id_text))
                false
            }
            TextUtils.isEmpty(et_li_password.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(true, resources.getString(R.string.error_password_text))
                false
            }
            else -> {
                true
            }
        }
    }

    fun userLoggedInSuccess() {
        hideProgressDialog()
        startActivity(Intent(this, NewsDisplayActivity::class.java))
        finish()
    }
}