package com.example.tvshows.tvshows

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.tvshows.MainActivity
import com.example.tvshows.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
//        Handler(Looper.getMainLooper()).postDelayed({
//            val mainIntent = Intent(this, MainActivity::class.java)
//            startActivity(mainIntent)
//            finish()
//        }, 3000)
        setStatusBarColor()
    }
    private fun setStatusBarColor() {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.windowBackGround)
    }
}