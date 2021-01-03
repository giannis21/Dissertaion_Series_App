package com.example.tvshows.ui.nowplaying

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.ui.favorites.FavoritesViewModel
import com.example.tvshows.ui.mostpopular.MostPopularViewModel
import com.example.tvshows.ui.search.SearchViewModel
import com.example.tvshows.ui.seen.SeenViewModel
import com.example.tvshows.ui.show_details.ShowDetailsViewModel
import com.example.tvshows.ui.toprated.TopRatedViewModel
import com.example.tvshows.ui.watchlist.WatchlistViewModel


@Suppress("UNCHECKED_CAST")
class NowPlayingViewModelFactory(private val remoteRepository: RemoteRepository, var context: Context) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NowPlayingViewModel::class.java) -> NowPlayingViewModel(remoteRepository,context) as T
            modelClass.isAssignableFrom(MostPopularViewModel::class.java) -> MostPopularViewModel(remoteRepository,context) as T
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> FavoritesViewModel(remoteRepository, context) as T
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(remoteRepository, context) as T
            modelClass.isAssignableFrom(SeenViewModel::class.java) -> SeenViewModel(remoteRepository, context) as T
            modelClass.isAssignableFrom(ShowDetailsViewModel::class.java) -> ShowDetailsViewModel(remoteRepository, context) as T
            modelClass.isAssignableFrom(TopRatedViewModel::class.java) -> TopRatedViewModel(remoteRepository, context) as T
            modelClass.isAssignableFrom(WatchlistViewModel::class.java) -> WatchlistViewModel(remoteRepository,context) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}

