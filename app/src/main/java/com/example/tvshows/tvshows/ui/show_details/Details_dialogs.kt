package com.example.tvshows.tvshows.ui.show_details

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.example.tvshows.R
import com.example.tvshows.ui.show_details.ShowDetailsViewModel
import com.example.tvshows.utils.Extension_Utils.Companion.success_toast
import kotlinx.android.synthetic.main.rating_dialog.view.*
import kotlinx.android.synthetic.main.season_overview_dialog.view.*

class Details_dialogs(var context: Context) {

    companion object {
        fun displaySeasonDialog(context: Context, overview: String) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            val layoutInflaterAndroid = LayoutInflater.from(context)
            val view2: View = layoutInflaterAndroid.inflate(R.layout.season_overview_dialog, null)
            builder.setView(view2)
            view2.season_overview.text = overview
            val alertDialog: AlertDialog = builder.create()
            alertDialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            alertDialog.show()
            view2.action_btn.setOnClickListener { alertDialog.dismiss() }
        }

        fun displayRatingDialog(context: Context,viewmodel:ShowDetailsViewModel) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            val layoutInflaterAndroid = LayoutInflater.from(context)
            val view2: View = layoutInflaterAndroid.inflate(R.layout.rating_dialog, null)
            builder.setView(view2)
            val alertDialog: AlertDialog = builder.create()
            alertDialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            alertDialog.show()
            alertDialog.setCancelable(false)
            view2.close.setOnClickListener { alertDialog.dismiss() }
            view2.submit.setOnClickListener {
                var rate=view2.rating_bar.rating
                rate = if(rate==0f) 1f else rate*2
                viewmodel.rateTvshow((rate).toString())
                alertDialog.dismiss()
            }
            view2.close.setOnClickListener {
                alertDialog.dismiss()
            }

        }

    }

    lateinit var alertDialog1: AlertDialog

    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
    val layoutInflaterAndroid = LayoutInflater.from(context)
    val view2: View = layoutInflaterAndroid.inflate(R.layout.loading_dialog, null)


    fun displayLoadingDialog(context: Context) {
        builder.setView(view2)
        alertDialog1 = builder.create()
        alertDialog1.show()

    }

    fun hideLoadingDialog() {
        alertDialog1.dismiss()
    }
}

