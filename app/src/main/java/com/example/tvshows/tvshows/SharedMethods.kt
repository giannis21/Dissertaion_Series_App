package com.example.tvshows.tvshows

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying
import com.example.tvshows.ui.nowplaying.NowPlayingFragment
import com.example.tvshows.ui.seen.MainViewModel
import com.google.gson.JsonObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object SharedMethods {
    var listOfActiveNot = mutableListOf<String>()

    @SuppressLint("SimpleDateFormat")
    fun getDateInMilli(releaseDate: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd")

        return try {
            val mDate: Date = sdf.parse(releaseDate)
            mDate.time
        } catch (e: ParseException) {
            0
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateInMilli1(releaseDate: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        return try {
            val mDate: Date = sdf.parse(releaseDate)
            mDate.time
        } catch (e: ParseException) {
            0
        }
    }

    fun filterList(list_allGenres: MutableList<Result_NowPlaying>): MutableList<Result_NowPlaying> {
        val tempList_filtered = mutableListOf<Result_NowPlaying>()
        tempList_filtered.clear()
        for (i in 0 until list_allGenres.size) {
            list_allGenres[i].genre_ids.forEach { current_genre ->
                for (selectedGenre in MainViewModel.selected_genres) {
                    if (selectedGenre.id == current_genre) {
                        tempList_filtered.add(list_allGenres[i])
                        break
                    }
                }
            }
        }
        return tempList_filtered
    }


    fun getPendingIntent(context: Context, id: String? = null): PendingIntent? {
        /*  val dataBundle = Bundle()
          dataBundle.putInt("id", id.toInt())
          dataBundle.putString("deriveFrom", "deepLink")

          return NavDeepLinkBuilder(context)
              .setGraph(R.navigation.mobile_navigation)
              .setDestination(R.id.showDetailsFragment)
              .setArguments(dataBundle)
              .createPendingIntent()
  */
        val resultIntent = Intent(context, SplashActivity::class.java)
        id?.let {
            resultIntent.putExtra("id", id)
            resultIntent.putExtra("deriveFrom", "deepLink")
        } ?: kotlin.run {
            resultIntent.putExtra("deriveFrom", "deepLink1")
        }


        resultIntent.action = Calendar.getInstance().timeInMillis.toString()
        resultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        return pendingIntent
    }
    fun playSound1(context: Context) {
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(context, notification)
        r.play()
    }


    fun vibratePhone(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26)
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 200, 300, 200), VibrationEffect.DEFAULT_AMPLITUDE))
    }
}