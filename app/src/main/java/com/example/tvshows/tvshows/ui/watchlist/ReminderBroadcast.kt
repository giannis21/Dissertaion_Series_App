package com.example.tvshows.tvshows.ui.watchlist

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.tvshows.R
import com.example.tvshows.tvshows.SharedMethods.getDateInMilli
import com.example.tvshows.ui.watchlist.WatchlistFragment
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ReminderBroadcast : BroadcastReceiver() {
    interface NotificationShow {
        fun notification_showed(id: String)
    }

    companion object {
        var notificationListener: NotificationShow? = null
    }

    var CHANNEL_ID = "channel1"
    override fun onReceive(context: Context, intent: Intent) {

        createNotificationChannel(context)

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
                .setContentIntent(getPendingIntent(context, id))
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
            notificationListener?.notification_showed(id)
        }

    }

    private fun getPendingIntent(context: Context, id: String): PendingIntent? {
        val dataBundle = Bundle()
        dataBundle.putInt("id", id.toInt())
        dataBundle.putString("deriveFrom", "deepLink")

        return NavDeepLinkBuilder(context)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.showDetailsFragment)
            .setArguments(dataBundle)
            .createPendingIntent()
    }


    private fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, "name", importance)
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}