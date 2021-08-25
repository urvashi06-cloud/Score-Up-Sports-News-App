package com.example.scoreup.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.example.scoreup.R
import com.example.scoreup.firestore.FirestoreClass
import com.example.scoreup.models.User
import com.example.scoreup.utils.Constants
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setUpActionBar()

        btn_settings_edit_choices.setOnClickListener(this)
        btn_settings_reminder.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_settings_edit_choices -> {
                    showChoicesMarked()
                }
                R.id.btn_settings_reminder -> {
                    val intent = Intent(this, ReminderActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun showChoicesMarked() {
        showProgressDialog(resources.getString(R.string.please_wait_text))
        FirestoreClass().getSportsPreferenceDetails(this)
    }

    fun successGetSportsPreferenceList(sports: ArrayList<ArrayList<String>>) {
        mIntent = Intent(this, ChoicesActivity::class.java)
        mIntent.putExtra(Constants.OTHER_SPORTS_PREFERENCES, sports[0])
        FirestoreClass().getUserDetails(this)
    }

    fun successGetUserDetails(user: User) {
        hideProgressDialog()
        mIntent.putExtra(Constants.OTHER_USER_DETAILS, user)
        startActivity(mIntent)
    }

    private fun setUpActionBar() {
        setSupportActionBar(tb_settings_back)

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow)

        tb_settings_back.setNavigationOnClickListener { onBackPressed() }
    }
}