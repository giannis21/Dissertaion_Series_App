package com.example.tvshows.tvshows.ui.show_details

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.example.tvshows.R
import kotlinx.android.synthetic.main.season_overview_dialog.view.*

object Season_dialog {

    fun displaySeasonDialog(context:Context,overview:String){
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

}