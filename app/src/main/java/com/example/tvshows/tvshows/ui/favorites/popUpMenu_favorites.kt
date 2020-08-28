package com.example.tvshows.tvshows.ui.favorites

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.navigation.Navigation


import com.example.tvshows.R
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.ui.favorites.FavoritesFragmentDirections
import com.example.tvshows.ui.favorites.FavoritesViewModel

object popUpMenu_favorites {

    fun showPopUpMenu_fav(
        context: Context?,
        menuItemView1: View,
        obj: TvShowDetails,
        viewModel: FavoritesViewModel
    ) {

        val ctw: Context = ContextThemeWrapper(context, R.style.popupTheme)
        val popup = PopupMenu(ctw, menuItemView1)

        popup.inflate(R.menu.popup_menu_fav)
        popup.show()

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.move_to_seen_menu_fav -> {
                    viewModel.moveToSeen(obj)
                    true
                }
                R.id.move_to_watchlist_menu_fav -> {
                    viewModel.moveTowatchlist(obj)
                    true
                }
                R.id.more_info_menu_fav -> {//.actionWatchlistToShowDetailsFragment(id, "watchList")
                    val action = FavoritesFragmentDirections.actionFavoritesToShowDetailsFragment(obj.id, "favorites")
                    Navigation.findNavController(menuItemView1).navigate(action)
                    true
                }
                else -> false
            }
        }
    }

}