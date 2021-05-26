package com.example.tvshows.tvshows.ui.watchlist

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.tvshows.R
import com.example.tvshows.tvshows.SharedMethods
import com.example.tvshows.tvshows.SharedMethods.getDateInMilli
import com.example.tvshows.tvshows.SharedMethods.playSound1
import com.example.tvshows.tvshows.SharedMethods.vibratePhone
import com.example.tvshows.tvshows.SplashActivity
import com.example.tvshows.tvshows.utils.PreferenceUtils
import org.json.JSONObject
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ReminderBroadcast : BroadcastReceiver() {
    interface NotificationShow {
        fun notification_showed(id: String, string: String)
    }

    companion object {
        var notificationListener: NotificationShow? = null
    }

    var CHANNEL_ID = "channel1"
    override fun onReceive(context: Context, intent: Intent) {



        val info = intent.extras?.getString("info")
        info?.let {
            val jsonObj = JSONObject(info)

            val id = jsonObj.getString("id")
            val dateInMilli = getDateInMilli(jsonObj.getString("release_date"))
            val notificationId = id.toInt() + dateInMilli.toInt()

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.seen)
                .setContentTitle(jsonObj.getString("name"))
                .setContentText("Episode ${jsonObj.getString("episode")} was released!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(SharedMethods.getPendingIntent(context, id))
                .setAutoCancel(true)

            if (PreferenceUtils.get_vibration_state(context)!!)
                vibratePhone(context)

            if (PreferenceUtils.get_sound_state(context)!!)
                playSound1(context)
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
            notificationListener?.notification_showed(id, jsonObj.getString("release_date"))
        }

    }






}