package com.example.tvshows.tvshows.ui.seen

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.navigation.Navigation


import com.example.tvshows.R
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.ui.seen.SeenFragmentDirections
import com.example.tvshows.ui.seen.SeenViewModel

object popUpMenu_topRated {

    fun showPopUpMenu_seen(
        context: Context?,
        menuItemView1: View,
        obj: TvShowDetails,
        viewModel: SeenViewModel
    ) {

        val ctw: Context = ContextThemeWrapper(context, R.style.popupTheme)
        val popup = PopupMenu(ctw, menuItemView1)

        popup.inflate(R.menu.popup_menu_seen)
        popup.show()

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.move_to_watchlist_menu_seen -> {
                    viewModel.moveToWatchlist(obj)

                    true
                }
                R.id.move_to_favorites_menu_seen -> {
                    viewModel.moveToFavorites(obj)

                    true
                }
                R.id.more_info_menu_seen -> {
                    val action = SeenFragmentDirections.actionSeenToShowDetailsFragment(obj.id, "seen")
                    Navigation.findNavController(menuItemView1).navigate(action)
                    true
                }
                else -> false
            }
        }
    }

}