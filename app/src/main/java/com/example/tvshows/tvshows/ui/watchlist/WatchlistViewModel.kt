package com.example.tvshows.ui.watchlist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvshows.TvShowRoomDatabase
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.local_repository
import com.example.tvshows.data.network.response.details.TvShowDetails
import kotlinx.coroutines.launch

class WatchlistViewModel(remoteRepository: RemoteRepository,var context: Context) : ViewModel() {

    val details: LiveData<MutableList<TvShowDetails>>
    private var local_repository: local_repository
    var countOfWatchlist: LiveData<Int>

    init {
        val TvshowsDao = TvShowRoomDatabase.getDatabase(context).tvShowDao()
        local_repository = local_repository(TvshowsDao)
        details=local_repository.getWatchlist()
        countOfWatchlist=local_repository.countTvShowsFromWatchlist()
    }

    fun deleteTvshow(id:Int){
        viewModelScope.launch {
               local_repository.deleteTvShowFromWatchlist(id.toString())
        }
    }


    fun moveFromwatchlistToFavorites(id:Int){
        viewModelScope.launch {
            local_repository.moveFromwatchlistToFavorites(id.toString())
        }
    }


    fun moveFromwatchlistToSeen(id:Int){
        viewModelScope.launch {
            local_repository.moveFromwatchlistToSeen(id.toString())
        }
    }
}