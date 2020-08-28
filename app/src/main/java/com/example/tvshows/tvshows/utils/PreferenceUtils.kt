package com.example.tvshows.tvshows.utils

import android.content.Context
import com.example.tvshows.tvshows.data.network.response.Guest_Session


class PreferenceUtils {
    companion object {
        //------------------------------VIBRATION-----------------------------------------------------//
        private const val TIME_ID = "com.frag.time"

        fun getLastTime(context: Context): String {
            val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(TIME_ID, "0")!!
        }

        fun setLastTime(lastTime: String, context: Context) {
            val editor = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(TIME_ID, lastTime)
            editor.apply()
        }

        private const val SESSION_ID = "com.frag.guest_session"
        fun getguest_session(context: Context):String{
            val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(SESSION_ID, "")!!
        }

        fun setguest_session(guestSession:String, context: Context) {
            val editor = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(SESSION_ID, guestSession)
            editor.apply()
        }
    }
}