package com.example.tvshows.tvshows.ui.watchlist

import android.view.View

interface ClickCallback {
    fun onClick(menuItemView1: View, id: Int)
    fun onDeleteIconClick(id: Int,name:String)

}