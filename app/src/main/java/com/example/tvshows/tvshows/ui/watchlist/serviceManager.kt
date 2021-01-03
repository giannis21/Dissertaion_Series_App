package com.example.tvshows.tvshows.ui.watchlist

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.tvshows.SharedMethods
import org.json.JSONObject


class serviceManager : Service() {
    var CHANNEL_ID= "channel1"


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var context=this
        println("arxise to sercixew")
        val info = intent?.extras?.getString("info")
        val jsonObj =  JSONObject(info)
        val intentReminder = Intent(context, ReminderBroadcast::class.java).apply {
            putExtra("info", info.toString())
            action = jsonObj.getString("id") + jsonObj.getString("release_date")
        }


        val pendingIntent = PendingIntent.getBroadcast(context, getNotificationId(jsonObj.getString("id"),jsonObj.getString("release_date")), intentReminder, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context?.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent)

        //    alarmManager.setExact(AlarmManager.RTC_WAKEUP, SharedMethods.getDateInMilli(jsonObj.getString("release_date")), pendingIntent)



        return START_STICKY
    }

    fun startNotificationAction(info: String,context: Context) {
        val jsonObj = JSONObject(info)
        val intentReminder = Intent(context, ReminderBroadcast::class.java).apply {
            putExtra("info", info.toString())
            action = jsonObj.getString("id") + jsonObj.getString("release_date")
        }

        val pendingIntent = PendingIntent.getBroadcast(context, getNotificationId(jsonObj.getString("id"),jsonObj.getString("release_date")), intentReminder, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context?.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")

    }

//
//    fun cancelAlarm(tvShow: TvShowDetails,context:Context) {
//
//        val intent = Intent(context, ReminderBroadcast::class.java)
//        intent.action = tvShow.id.toString() + tvShow.next_episode_to_air?.air_date.toString()
//        val alarmIntent = PendingIntent.getBroadcast(context, getNotificationId(tvShow.id.toString(),tvShow.next_episode_to_air?.air_date.toString()), intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        val alarmMgr = context?.getSystemService(ALARM_SERVICE) as AlarmManager
//        alarmMgr.cancel(alarmIntent)
//    }

    private fun getNotificationId(id: String,release_date:String): Int {
         val dateInMilli= SharedMethods.getDateInMilli(release_date)
        return id.toInt()+dateInMilli.toInt()
    }


}