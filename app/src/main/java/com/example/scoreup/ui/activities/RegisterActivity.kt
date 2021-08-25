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
import com.example.scoreup.models.User
import com.example.scoreup.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else{
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setUpActionBar()

        tv_register_log_in.setOnClickListener(this)
        btn_register_submit.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if (v != null){
            when (v.id){
                R.id.tv_register_log_in -> onBackPressed()
                R.id.btn_register_submit -> registerSubmitPressed()
            }
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(tb_register_back)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        }
        tb_register_back.setNavigationOnClickListener { onBackPressed() }
    }

    private fun registerSubmitPressed() {
        if (validateRegisterEntries()){
            showProgressDialog(resources.getString(R.string.please_wait_text))

            val emailId: String = et_register_email_id.text.toString().trim { it <= ' ' }
            val password: String = et_register_password.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailId, password)
                .addOnCompleteListener {task ->
                    if (task.isSuccessful){
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val user: User = User(
                            id = firebaseUser.uid,
                            first_name = et_register_first_name.text.toString().trim{ it <= ' ' },
                            last_name = et_register_last_name.text.toString().trim { it <= ' ' },
                            email_id = emailId
                        )

                        FirestoreClass().registerUser(this, user)

                    } else{
                        hideProgressDialog()
                        showSnackBar(true, task.exception!!.message.toString())
                }
            }
        }
    }

    private fun validateRegisterEntries(): Boolean{
        return when{
            TextUtils.isEmpty(et_register_first_name.text.toString().trim{it <= ' '}) -> {
                showSnackBar(true, resources.getString(R.string.error_first_name_text))
                false
            }
            TextUtils.isEmpty(et_register_last_name.text.toString().trim{it <= ' '}) -> {
                showSnackBar(true, resources.getString(R.string.error_last_name_text))
                false
            }
            TextUtils.isEmpty(et_register_email_id.text.toString().trim{it <= ' '}) -> {
                showSnackBar(true, resources.getString(R.string.error_email_id_text))
                false
            }
            TextUtils.isEmpty(et_register_password.text.toString().trim{it <= ' '}) -> {
                showSnackBar(true, resources.getString(R.string.error_password_text))
                false
            }
            et_register_password.text.toString().trim{it <= ' '}.length < 8 -> {
                showSnackBar(true, resources.getString(R.string.error_passwrod_length_text))
                false
            }
            TextUtils.isEmpty(et_register_confirm_password.text.toString().trim{it <= ' '}) -> {
                showSnackBar(true, resources.getString(R.string.error_confirm_password_text))
                false
            }
            et_register_password.text.toString().trim{it <= ' '} != et_register_confirm_password.text.toString().trim{it <= ' '} -> {
                showSnackBar(true, resources.getString(R.string.error_passwords_mismatch_text))
                false
            }
            !cb_register_tnc.isChecked -> {
                showSnackBar(true, resources.getString(R.string.error_tnc_unchecked_text))
                false
            }
            else ->{
                true
            }
        }
    }

    fun userRegistrationSuccess(user: User){
        hideProgressDialog()
        val intent = Intent(this, ChoicesActivity::class.java)
        intent.putExtra(Constants.OTHER_USER_DETAILS, user)
        startActivity(intent)
        finish()
    }
}