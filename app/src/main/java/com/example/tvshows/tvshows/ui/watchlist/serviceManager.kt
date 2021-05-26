package com.example.tvshows.tvshows.ui.watchlist

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tvshows.R
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.tvshows.SharedMethods
import com.example.tvshows.tvshows.SharedMethods.getPendingIntent
import com.example.tvshows.tvshows.SharedMethods.playSound1
import com.example.tvshows.tvshows.SharedMethods.vibratePhone
import com.example.tvshows.tvshows.utils.PreferenceUtils
import com.example.tvshows.tvshows.utils.PreferenceUtils.Companion.get_sound_state
import com.example.tvshows.tvshows.utils.PreferenceUtils.Companion.get_vibration_state
import org.json.JSONObject


class serviceManager : Service() {
    var CHANNEL_ID = "channel1"
    var context = this

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle("TvShows Notification")
            .setContentText("Notification Pending")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getPendingIntent(context))
            .setAutoCancel(true)
            .build()

        startForeground(-1, builder)
        var i = 1
        SharedMethods.listOfActiveNot.forEach {
            val info = it
            val jsonObj = JSONObject(info)
            val intentReminder = Intent(this.baseContext, ReminderBroadcast::class.java).apply {
                putExtra("info", info)
                action = jsonObj.getString("id") + jsonObj.getString("release_date")
            }
            i++
            val pendingIntent = PendingIntent.getBroadcast(
                this.baseContext,
                getNotificationId(jsonObj.getString("id"), jsonObj.getString("release_date")),
                intentReminder,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = this.baseContext?.getSystemService(ALARM_SERVICE) as AlarmManager
//            alarmManager.setExact(
//                AlarmManager.RTC_WAKEUP,
//                System.currentTimeMillis() + 5000 * i,
//                pendingIntent
//            )
            val preferredTime1 = PreferenceUtils.get_preferred_time(context)!!.split(":")
            val time = preferredTime1[0]
            val minutes = preferredTime1[1]
            val temp =jsonObj.getString("release_date") + " " + time + ":" + minutes + ":10" //παιρνω την ημερομηνια που βγαινει το επεισοδιο και προσθετω την ωρα που εχω ορισει εγω στις ρυθμισεις

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, SharedMethods.getDateInMilli1(temp), pendingIntent)
        }





        return START_STICKY
    }

    fun startNotificationAction(info: String, context: Context) {
        val jsonObj = JSONObject(info)
        val intentReminder = Intent(context, ReminderBroadcast::class.java).apply {
            putExtra("info", info.toString())
            action = jsonObj.getString("id") + jsonObj.getString("release_date")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getNotificationId(jsonObj.getString("id"), jsonObj.getString("release_date")),
            intentReminder,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context?.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")

    }


    private fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, "name", importance)
            if (get_vibration_state(context)!!)
                vibratePhone(context)

            if (get_sound_state(context)!!)
                playSound1(context)
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getNotificationId(id: String, release_date: String): Int {
        val dateInMilli = SharedMethods.getDateInMilli(release_date)
        return id.toInt() + dateInMilli.toInt()
    }



}
//package com.example.tvshows.tvshows.ui.watchlist
//
//import android.app.AlarmManager
//import android.app.PendingIntent
//import android.app.job.JobParameters
//import android.app.job.JobService
//import android.content.Intent
//import com.example.tvshows.tvshows.SharedMethods
//import org.json.JSONObject
//
//
//class serviceManager : JobService() {
//    private var jobCancelled = false
//
//    override fun onStartJob(params: JobParameters?): Boolean {
//
//        Thread(Runnable {
//            if (!jobCancelled) {
//
//
//            SharedMethods.listOfActiveNot.forEach {
//                val info = it
//                val jsonObj = JSONObject(info)
//                val intentReminder = Intent(this.baseContext, ReminderBroadcast::class.java).apply {
//                    putExtra("info", info)
//                    action = jsonObj.getString("id") + jsonObj.getString("release_date")
//                }
//
//                val pendingIntent = PendingIntent.getBroadcast(
//                    this.baseContext, getNotificationId(
//                        jsonObj.getString(
//                            "id"
//                        ), jsonObj.getString("release_date")
//                    ), intentReminder, PendingIntent.FLAG_UPDATE_CURRENT
//                )
//                val alarmManager = this.baseContext?.getSystemService(ALARM_SERVICE) as AlarmManager
//                alarmManager.setExact(
//                    AlarmManager.RTC_WAKEUP,
//                    System.currentTimeMillis() + 10000,
//                    pendingIntent
//                )
//            }
//             jobFinished(params, true)
//            }
//        }).start()
//        return false
//    }
//
//    override fun onStopJob(params: JobParameters?): Boolean {
//        jobCancelled = true;
//        return true
//    }
//    private fun getNotificationId(id: String, release_date: String): Int {
//         val dateInMilli= SharedMethods.getDateInMilli(release_date)
//        return id.toInt()+dateInMilli.toInt()
//    }
//
//}