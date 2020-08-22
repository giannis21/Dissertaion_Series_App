package com.example.tvshows.tvshows.ui.favorites

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController


import com.example.tvshows.R
import com.example.tvshows.ui.favorites.FavoritesFragmentDirections
import com.example.tvshows.ui.favorites.FavoritesViewModel
import com.example.tvshows.ui.watchlist.WatchlistFragmentDirections
import com.example.tvshows.ui.watchlist.WatchlistViewModel

object popUpMenu_favorites {

    fun showPopUpMenu_fav(context: Context?,menuItemView1: View,id:Int,viewModel:FavoritesViewModel){

        val ctw : Context = ContextThemeWrapper(context, R.style.popupTheme)
        val popup = PopupMenu(ctw, menuItemView1)

        popup.inflate(R.menu.popup_menu_fav)
        popup.show()

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.move_to_seen_menu_fav -> {
                    viewModel.moveFromwatchlistToSeen(id)
                    true
                }
                R.id.move_to_watchlist_menu_fav -> {
                    viewModel.moveFromFavoritesTowatchlist(id)
                    true
                }
                R.id.more_info_menu_fav -> {//.actionWatchlistToShowDetailsFragment(id, "watchList")
                    val action = FavoritesFragmentDirections.actionFavoritesToShowDetailsFragment(id, "favorites")
                    Navigation.findNavController(menuItemView1).navigate(action)
                    true
                }
                else -> false
            }
        }
    }

}