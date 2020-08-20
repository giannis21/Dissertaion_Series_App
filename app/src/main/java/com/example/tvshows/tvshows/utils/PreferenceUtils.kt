package com.example.tvshows.tvshows.utils

import android.content.Context


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


    }
}