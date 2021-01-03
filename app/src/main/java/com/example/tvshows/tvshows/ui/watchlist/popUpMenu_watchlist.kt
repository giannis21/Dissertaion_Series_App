package com.example.tvshows.tvshows.ui.watchlist

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.navigation.Navigation


import com.example.tvshows.R
import com.example.tvshows.TvShowRoomDatabase
import com.example.tvshows.data.local_repository
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.tvshows.ui.favorites.PopUpMenuStates
import com.example.tvshows.ui.watchlist.WatchlistFragmentDirections
import com.example.tvshows.ui.watchlist.WatchlistViewModel

object popUpMenu_watchlist {

    fun showPopUpMenu(context: Context?, menuItemView1: View, obj: (PopUpMenuStates) -> Unit) {

        val ctw: Context = ContextThemeWrapper(context, R.style.popupTheme)
        val popup = PopupMenu(ctw, menuItemView1)

        popup.inflate(R.menu.popup_menu)
        popup.show()

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.move_to_seen_menu -> {
                    obj.invoke(PopUpMenuStates.moveToSeen)
                    true
                }
                R.id.move_to_favorites_menu -> {
                    obj.invoke(PopUpMenuStates.move_to_favorites)
                    true
                }
                R.id.more_info_menu -> {
                    obj.invoke(PopUpMenuStates.moreInfo_menu)
                    true
                }
                else -> false
            }
        }
    }

}

