package com.example.tvshows.tvshows

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import com.example.tvshows.MainActivity
import com.example.tvshows.R
import com.example.tvshows.ui.show_details.ShowDetailsFragmentArgs
import com.example.tvshows.ui.show_details.ShowDetailsFragmentArgs.fromBundle

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // val mainIntent = Intent(this, MainActivity::class.java)
        //  startActivity(mainIntent)
        setStatusBarColor()

        intent?.extras?.getString("deriveFrom")?.let {

            intent?.extras?.getString("id")?.let {

                val mainIntent = Intent(this, MainActivity::class.java)
                mainIntent.putExtra("id", intent?.extras?.getString("id"))
                mainIntent.putExtra("deriveFrom", "deepLink")
                startActivity(mainIntent)
                finish()

            } ?: kotlin.run {

                val mainIntent = Intent(this, MainActivity::class.java)
                mainIntent.putExtra("deriveFrom", "deepLinkWatchlist")
                startActivity(mainIntent)
                finish()

            }

        } ?: kotlin.run {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
//            Handler(Looper.getMainLooper()).postDelayed({
//                val mainIntent = Intent(this, MainActivity::class.java)
//                startActivity(mainIntent)
//                finish()
//            }, 3000)
        }


    }

    private fun setStatusBarColor() {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.windowBackGround)
    }
}