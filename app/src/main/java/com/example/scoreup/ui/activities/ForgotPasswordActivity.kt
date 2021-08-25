package com.example.scoreup.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.scoreup.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setUpActionBar()

        btn_fp_submit.setOnClickListener { forgotPasswordSubmitPressed() }
    }

    private fun setUpActionBar() {
        setSupportActionBar(tb_fp_back)

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow)

        tb_fp_back.setNavigationOnClickListener { onBackPressed() }
    }

    private fun forgotPasswordSubmitPressed() {
        if (TextUtils.isEmpty(et_fp_email_id.text.toString().trim { it <= ' ' })) {
            showSnackBar(true, resources.getString(R.string.error_email_id_text))
        } else {
            showProgressDialog(resources.getString(R.string.please_wait_text))
            val email = et_fp_email_id.text.toString().trim { it <= ' ' }
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    hideProgressDialog()
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "The reset password email has been sent to your email ID.",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        showSnackBar(true, it.exception!!.message.toString())
                    }
                }
        }
    }
}