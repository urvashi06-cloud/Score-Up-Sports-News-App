package com.example.scoreup.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Sports(
    val user_id: String = "",
    var sports_preference_id: String = "",
    val selectedSports: ArrayList<String> = ArrayList()
) : Parcelable