package com.example.tvshows.tvshows.utils

import android.content.Context
import androidx.preference.PreferenceManager
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


        //------------------------------VIBRATION-----------------------------------------------------//
        private const val VIBRATION_ID = "com.frag.vibration"

        fun get_vibration_state(context: Context): Boolean? {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getBoolean(VIBRATION_ID, false)
        }

        fun set_vibration_state(vibration_state: Boolean, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putBoolean(VIBRATION_ID, vibration_state)
            editor.apply()
        }
//----------------------------------SOUND-----------------------------------------------------//

        private const val SOUND_ID = "com.frag.sound"

        fun get_sound_state(context: Context): Boolean? {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getBoolean(SOUND_ID, false)
        }

        fun set_sound_state(sound_state: Boolean, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putBoolean(SOUND_ID, sound_state)
            editor.apply()
        }

//----------------------------------TIME PICKER-----------------------------------------------------//

        private const val TIME_PICKER_ID = "com.frag.time_picker"

        fun get_preferred_time(context: Context): String? {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(TIME_PICKER_ID, "19:00")
        }

        fun set_preferred_time(preferred_time: String, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(TIME_PICKER_ID, preferred_time)
            editor.apply()
        }

        //----------------------------------CHECK IF APP IS OPENED-----------------------------------------------------//
        private const val APP_STATE = "com.frag.app_state"

        fun get_app_State(context: Context): String? {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
            return preferences.getString(APP_STATE, "")
        }

        fun set_app_State(app_state: String, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context.applicationContext).edit()
            editor.putString(APP_STATE, app_state)
            editor.apply()
        }

    }
}