package com.example.tvshows.utils

import android.content.Context
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.widget.NestedScrollView

class Extension_Utils {
    companion object {

        fun NestedScrollView.setVisible() {
            this.visibility = View.VISIBLE
        }
        fun NestedScrollView.setGone() {
            this.visibility = View.GONE
        }
        fun ProgressBar.setVisible() {
            this.visibility = View.VISIBLE
        }
        fun ProgressBar.setGone() {
            this.visibility = View.GONE
        }
        fun HorizontalScrollView.setVisible(){
            this.visibility=View.VISIBLE
        }
        fun HorizontalScrollView.setGone(){
            this.visibility=View.GONE
        }
        fun RelativeLayout.setGone(){
            this.visibility=View.GONE
        }
        fun RelativeLayout.setVisible(){
            this.visibility=View.VISIBLE
        }
        fun HorizontalScrollView.iSVisible():Boolean{
            return this.visibility==View.VISIBLE
        }
        fun Context.toast(message:String){
            Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
        }

    }
}