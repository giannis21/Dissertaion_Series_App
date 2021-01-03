package com.example.tvshows.tvshows.data.network.response

data class RateResponse(
    val status_code: Int,
    val status_message: String,
    val success: Boolean
){
    override fun toString(): String {
        return status_message
    }
}