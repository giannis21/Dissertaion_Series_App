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
        obj: (PopUpMenuStates) -> Unit
    ) {

        val ctw: Context = ContextThemeWrapper(context, R.style.popupTheme)
        val popup = PopupMenu(ctw, menuItemView1)

        popup.inflate(R.menu.popup_menu_fav)
        popup.show()

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.move_to_seen_menu_fav -> {
                    obj.invoke(PopUpMenuStates.moveToSeen)
                    true
                }
                R.id.move_to_watchlist_menu_fav -> {
                    obj.invoke(PopUpMenuStates.moveTowatchlist)
                    true
                }
                R.id.more_info_menu_fav -> {
                    obj.invoke(PopUpMenuStates.moreInfo_menu)
                    true
                }
                else -> false
            }
        }
    }

}

enum class PopUpMenuStates {
    moveToSeen,
    moveTowatchlist,
    moreInfo_menu,
    move_to_favorites
}