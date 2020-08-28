package com.example.tvshows.tvshows.data.network.response

data class Guest_Session(
    val expires_at: String,
    val guest_session_id: String,
    val success: Boolean
)