package com.example.tvshows.utils

import android.content.Context
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import es.dmoral.toasty.Toasty

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
        fun Context.success_toast(message:String){
            Toasty.success(applicationContext, message, Toast.LENGTH_SHORT, true).show()
         }

        fun Context.error_toast(message:String){
            Toasty.error(applicationContext, message, Toast.LENGTH_SHORT, true).show()
        }

        fun Context.info_toast(message:String){
            Toasty.info(applicationContext, message, Toast.LENGTH_SHORT, true).show()
        }

        fun Context.warning_toast(message:String){
            Toasty.warning(applicationContext, message, Toast.LENGTH_SHORT, true).show()
        }


    }
}