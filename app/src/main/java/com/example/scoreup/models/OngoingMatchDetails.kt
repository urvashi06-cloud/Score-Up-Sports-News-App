package com.example.scoreup.models

data class OngoingMatchDetails(
    val sport: String,
    val image: String = "",
    val match_type: String = "",
    val team_1: String = "",
    val team_2: String = "",
    val score: String = "",
    val other_details: String = "",
    val location: String = "",
    val status: String = "",
    val last_updated: String = ""
)