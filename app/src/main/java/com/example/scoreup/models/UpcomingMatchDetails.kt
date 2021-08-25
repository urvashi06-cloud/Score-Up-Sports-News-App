package com.example.scoreup.models

data class UpcomingMatchDetails(
    val sport: String,
    val image: String = "",
    val match_type: String = "",
    val team_1: String = "",
    val team_2: String = "",
    val location: String = "",
    val starts_at: String = "",
    var reminder: Boolean = false
)