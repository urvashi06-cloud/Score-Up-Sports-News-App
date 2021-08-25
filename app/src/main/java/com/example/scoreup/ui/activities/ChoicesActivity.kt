package com.example.scoreup.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.example.scoreup.R
import com.example.scoreup.firestore.FirestoreClass
import com.example.scoreup.models.Sports
import com.example.scoreup.models.User
import com.example.scoreup.utils.Constants
import kotlinx.android.synthetic.main.activity_choices.*

class ChoicesActivity : BaseActivity() {

    private val sportsSelectedArrayList: ArrayList<String> = ArrayList()
    private lateinit var mUserDetails: User
    private var mSportsPreferences: ArrayList<String> = ArrayList(0)

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choices)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        if (intent.hasExtra(Constants.OTHER_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.OTHER_USER_DETAILS)!!
        }
        if (intent.hasExtra(Constants.OTHER_SPORTS_PREFERENCES)) {
            mSportsPreferences =
                intent.getStringArrayListExtra(Constants.OTHER_SPORTS_PREFERENCES)!!
        }

        Log.i("UserDetailsParcelSucces", "User Details = $mUserDetails")

        if (mSportsPreferences.isNotEmpty() && mUserDetails.choices_registered_once) {
            setUpActionBar()

            if (Constants.CRICKET in mSportsPreferences)
                cb_choices_cricket.isChecked = true
            if (Constants.BASKETBALL in mSportsPreferences)
                cb_choices_basketball.isChecked = true
            if (Constants.BADMINTON in mSportsPreferences)
                cb_choices_badminton.isChecked = true
            if (Constants.TENNIS in mSportsPreferences)
                cb_choices_tennis.isChecked = true
            if (Constants.TABLE_TENNIS in mSportsPreferences)
                cb_choices_table_tennis.isChecked = true
            if (Constants.FOOTBALL in mSportsPreferences)
                cb_choices_football.isChecked = true
            if (Constants.ICE_HOCKEY in mSportsPreferences)
                cb_choices_ice_hockey.isChecked = true
            if (Constants.SWIMMING in mSportsPreferences)
                cb_choices_swimming.isChecked = true
        }

        btn_choices_save.setOnClickListener {
            choicesSaveButtonPressed()
        }

    }

    private fun setUpActionBar() {
        setSupportActionBar(tb_choices_back)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        }
        tb_choices_back.setNavigationOnClickListener { onBackPressed() }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun choicesSaveButtonPressed() {
        if (noErrorInCheckBoxes()) {
            showProgressDialog(resources.getString(R.string.please_wait_text))

            if (!mUserDetails.choices_registered_once) {
                val sportsSelected: Sports = Sports(
                    user_id = FirestoreClass().getCurrentUserId(),
                    selectedSports = sportsSelectedArrayList
                )
                FirestoreClass().addSportsPreference(this, sportsSelected)
            } else {
                FirestoreClass().getSportsPreferenceDetails(this)
            }
        }
    }


    private fun noErrorInCheckBoxes(): Boolean {
        var numberOfChoicesSelected: Int = 0
        if (cb_choices_basketball.isChecked) {
            numberOfChoicesSelected += 1
            sportsSelectedArrayList.add(Constants.BASKETBALL)
        }
        if (cb_choices_badminton.isChecked) {
            numberOfChoicesSelected += 1
            sportsSelectedArrayList.add(Constants.BADMINTON)
        }
        if (cb_choices_tennis.isChecked) {
            numberOfChoicesSelected += 1
            sportsSelectedArrayList.add(Constants.TENNIS)
        }
        if (cb_choices_swimming.isChecked) {
            numberOfChoicesSelected += 1
            sportsSelectedArrayList.add(Constants.SWIMMING)
        }
        if (cb_choices_cricket.isChecked) {
            numberOfChoicesSelected += 1
            sportsSelectedArrayList.add(Constants.CRICKET)
        }
        if (cb_choices_football.isChecked) {
            numberOfChoicesSelected += 1
            sportsSelectedArrayList.add(Constants.FOOTBALL)
        }
        if (cb_choices_table_tennis.isChecked) {
            numberOfChoicesSelected += 1
            sportsSelectedArrayList.add(Constants.TABLE_TENNIS)
        }
        if (cb_choices_ice_hockey.isChecked) {
            numberOfChoicesSelected += 1
            sportsSelectedArrayList.add(Constants.ICE_HOCKEY)
        }

        return if (numberOfChoicesSelected == 0) {
            showSnackBar(true, "You have to select at least one of the sports.")
            false
        } else
            true
    }

    fun choicesSuccessfullyRegistered() {
        hideProgressDialog()
        FirestoreClass().updateUserSportsPreferenceEntered(this, mUserDetails.id)
    }

    fun successUpdateUserSportsPreferences() {
        startActivity(Intent(this, LogInActivity::class.java))
        finish()
    }

    fun successGetSportsPreferenceId(sportsId: ArrayList<String>) {
        FirestoreClass().updateSportsPreferencesList(this, sportsId[0], sportsSelectedArrayList)
    }

    fun successUpdateSportsPreferenceList() {
        hideProgressDialog()
        onBackPressed()
    }
}