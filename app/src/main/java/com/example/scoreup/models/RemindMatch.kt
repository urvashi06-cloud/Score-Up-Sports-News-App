package com.example.scoreup.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RemindMatch(
    val sport: String = "",
    val match_type: String = "",
    val team_1: String = "",
    val team_2: String = "",
    var started: String = "false",
    val starts_at: String = "",
    val user_id: String = "",
    var remind_match_id: String = ""
): Parcelable