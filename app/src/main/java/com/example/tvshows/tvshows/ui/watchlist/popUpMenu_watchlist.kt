package com.example.tvshows.tvshows.ui.watchlist

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.navigation.Navigation


import com.example.tvshows.R
import com.example.tvshows.ui.watchlist.WatchlistFragmentDirections
import com.example.tvshows.ui.watchlist.WatchlistViewModel

object popUpMenu_watchlist {

    fun showPopUpMenu(context: Context?,menuItemView1: View,id:Int,viewModel:WatchlistViewModel){

        val ctw :Context= ContextThemeWrapper(context, R.style.popupTheme)
        val popup = PopupMenu(ctw, menuItemView1)

        popup.inflate(R.menu.popup_menu)
        popup.show()

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.move_to_seen_menu -> {
                    viewModel.moveFromwatchlistToSeen(id)
                    true
                }
                R.id.move_to_favorites_menu -> {
                    viewModel.moveFromwatchlistToFavorites(id)
                    true
                }
                R.id.more_info_menu -> {
                    val action = WatchlistFragmentDirections.actionWatchlistToShowDetailsFragment(id, "watchList")
                    Navigation.findNavController(menuItemView1).navigate(action)
                    true
                }
                else -> false
            }
        }
    }

}