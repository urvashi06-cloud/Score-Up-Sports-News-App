package com.example.scoreup.firestore

import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.scoreup.models.RemindMatch
import com.example.scoreup.models.Sports
import com.example.scoreup.models.User
import com.example.scoreup.ui.activities.*
import com.example.scoreup.ui.fragments.OngoingFragment
import com.example.scoreup.ui.fragments.UpcomingFragment
import com.example.scoreup.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFirestore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess(userInfo)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.d(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    it
                )

                Toast.makeText(
                    activity,
                    "Not registered. An error was encountered.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    fun getUserDetails(activity: Activity) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, it.toString())

                val user = it.toObject(User::class.java)!!
                when (activity) {
                    is LogInActivity -> {
                        activity.userLoggedInSuccess()
                    }
                    is SettingsActivity -> {
                        activity.successGetUserDetails(user)
                    }
                }

            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName, it.message.toString(), it)

                Toast.makeText(
                    activity,
                    it.message.toString(),
                    Toast.LENGTH_LONG
                ).show()

                when (activity) {
                    is LogInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun addSportsPreference(activity: ChoicesActivity, sportsSelected: Sports) {
        mFirestore.collection(Constants.SPORTS_PREFERENCE)
            .document()
            .set(sportsSelected, SetOptions.merge())
            .addOnSuccessListener {
                activity.choicesSuccessfullyRegistered()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error occurred while registering choices.",
                    it
                )
                Toast.makeText(
                    activity,
                    "Error occurred while registering the choices.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getSportsPreferenceDetails(
        activity: Activity,
        fragment: Fragment? = null,
        popupMenu: PopupMenu? = null,
        sportsSelected: Boolean = false
    ) {
        mFirestore.collection(Constants.SPORTS_PREFERENCE)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                Log.i("UserSportsPreferences", it.documents.toString())
                val sportsPreferenceArray = ArrayList<ArrayList<String>>()
                val sportPreferenceIdArray = ArrayList<String>()
                for (i in it.documents) {
                    val sport = i.toObject(Sports::class.java)!!
                    sport.sports_preference_id = i.id
                    sportPreferenceIdArray.add(sport.sports_preference_id)
                    sportsPreferenceArray.add(sport.selectedSports)
                }
                when (activity) {
                    is SettingsActivity -> {
                        activity.successGetSportsPreferenceList(sportsPreferenceArray)
                    }
                    is ChoicesActivity -> {
                        activity.successGetSportsPreferenceId(sportPreferenceIdArray)
                    }
                }
                when (fragment) {
                    is OngoingFragment -> {
                        if (sportsSelected) {
                            fragment.successGetSportsPreferenceList(
                                sportsPreferenceArray,
                                popupMenu
                            )
                        } else {
                            fragment.successSportsNotSelected(sportsPreferenceArray)
                        }
                    }
                    is UpcomingFragment -> {
                        if (sportsSelected) {
                            fragment.successGetSportsPreferenceList(
                                sportsPreferenceArray,
                                popupMenu
                            )
                        } else {
                            fragment.successSportsNotSelected(sportsPreferenceArray)
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e(
                    "UserSportsPreferences",
                    "An error occurred while getting user's sports preferences.",
                    it
                )
                when (activity) {
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                when (fragment) {
                    is OngoingFragment -> {
                        fragment.hideProgressDialog()
                    }
                    is UpcomingFragment -> {
                        fragment.hideProgressDialog()
                    }
                }
            }
    }

    fun updateUserSportsPreferenceEntered(activity: Activity, userId: String) {
        mFirestore.collection(Constants.USERS)
            .document(userId)
            .update(Constants.CHOICES_REGISTERED_ONCE, true)
            .addOnSuccessListener {
                when (activity) {
                    is ChoicesActivity -> {
                        activity.successUpdateUserSportsPreferences()
                    }
                }
            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName, it.message.toString(), it)

                Toast.makeText(
                    activity,
                    it.message.toString(),
                    Toast.LENGTH_LONG
                ).show()

                when (activity) {
                    is ChoicesActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun updateSportsPreferencesList(
        activity: Activity,
        sportsId: String,
        selectedSports: ArrayList<String>
    ) {
        mFirestore.collection(Constants.SPORTS_PREFERENCE)
            .document(sportsId)
            .update(Constants.SELECTED_SPORTS, selectedSports)
            .addOnSuccessListener {
                when (activity) {
                    is ChoicesActivity -> {
                        activity.successUpdateSportsPreferenceList()
                    }
                }
            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName, it.message.toString(), it)

                Toast.makeText(
                    activity,
                    it.message.toString(),
                    Toast.LENGTH_LONG
                ).show()

                when (activity) {
                    is ChoicesActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun addMatchToReminder(fragment: Fragment, matchDetails: RemindMatch) {
        mFirestore.collection(Constants.REMIND_MATCH)
            .document()
            .set(matchDetails, SetOptions.merge())
            .addOnSuccessListener {
                when (fragment) {
                    is UpcomingFragment -> {
                        fragment.successAddMatchToReminder()
                    }
                }
            }
            .addOnFailureListener {
                Log.e(fragment.javaClass.simpleName, it.message.toString(), it)

                Toast.makeText(
                    fragment.activity,
                    it.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    fun getListOfRemindMatches(activity: Activity) {
        mFirestore.collection(Constants.REMIND_MATCH)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                //Log.i("RemindMatches", it.documents.toString())
                val remindMatchesList: ArrayList<RemindMatch> = ArrayList()
                for (i in it.documents) {
                    val remindMatches = i.toObject(RemindMatch::class.java)!!
                    remindMatches.remind_match_id = i.id
                    remindMatchesList.add(remindMatches)
                }

                when (activity) {
                    is ReminderActivity -> {
                        activity.successGetListOfRemindMatches(remindMatchesList)
                    }
                }
            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName, it.message.toString(), it)

                Toast.makeText(
                    activity,
                    it.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
                when (activity) {
                    is ReminderActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun removeMatchFromRemindMatch(activity: Activity, matchDetails: RemindMatch) {
        mFirestore.collection(Constants.REMIND_MATCH)
            .document(matchDetails.remind_match_id)
            .delete()
            .addOnSuccessListener {
                when (activity) {
                    is ReminderActivity -> {
                        activity.getListOfRemindMatches()
                    }
                }
            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName, it.message.toString(), it)

                Toast.makeText(
                    activity,
                    it.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}