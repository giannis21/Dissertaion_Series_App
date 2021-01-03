package com.example.tvshows.tvshows.ui.callbacks

import android.view.View
import com.example.tvshows.data.network.response.details.TvShowDetails

interface ClickCallback {
    fun onClick(menuItemView1: View, id: TvShowDetails)
    fun onDeleteIconClick(id: Int,name:String)
    fun onNotificationIconClick(tvShow:TvShowDetails)
}