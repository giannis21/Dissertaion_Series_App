package com.example.tvshows.tvshows.ui.seen

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.navigation.Navigation


import com.example.tvshows.R
import com.example.tvshows.ui.favorites.FavoritesViewModel
import com.example.tvshows.ui.seen.SeenFragmentDirections
import com.example.tvshows.ui.seen.SeenViewModel
import com.example.tvshows.ui.watchlist.WatchlistFragmentDirections
import com.example.tvshows.ui.watchlist.WatchlistViewModel

object popUpMenu_topRated {

    fun showPopUpMenu_seen(
        context: Context?,
        menuItemView1: View,
        id:Int,
        viewModel: SeenViewModel
    ){

        val ctw : Context = ContextThemeWrapper(context, R.style.popupTheme)
        val popup = PopupMenu(ctw, menuItemView1)

        popup.inflate(R.menu.popup_menu_seen)
        popup.show()

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.move_to_watchlist_menu_seen -> {
                    viewModel.moveFromSeenToWatchlist(id)
                    true
                }
                R.id.move_to_favorites_menu_seen -> {
                    viewModel.moveFromSeenToFavorites(id)
                    true
                }
                R.id.more_info_menu_seen -> {
                    val action = SeenFragmentDirections.actionSeenToShowDetailsFragment(id, "seen")
                    Navigation.findNavController(menuItemView1).navigate(action)
                    true
                }
                else -> false
            }
        }
    }

}