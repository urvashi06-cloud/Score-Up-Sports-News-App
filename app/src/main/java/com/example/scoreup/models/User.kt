package com.example.scoreup.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val first_name: String = "",
    val last_name: String = "",
    val email_id: String = "",
    val phone: String = "",
    val image: String = "",
    var choices_registered_once: Boolean = false
) : Parcelable