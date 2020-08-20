package com.example.tvshows.ui.favorites

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvshows.TvShowRoomDatabase
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.local_repository
import com.example.tvshows.data.network.response.details.TvShowDetails
import kotlinx.coroutines.launch

class FavoritesViewModel(remoteRepository: RemoteRepository, context: Context) : ViewModel() {

    val details: LiveData<MutableList<TvShowDetails>>
    private var local_repository: local_repository
    var countOfFav: LiveData<Int>

    init {
        val TvshowsDao = TvShowRoomDatabase.getDatabase(context).tvShowDao()
        local_repository = local_repository(TvshowsDao)
        details=local_repository.getFavorites()
        countOfFav=local_repository.countTvShowsFromFavorites()
    }

    fun deleteTvshow(id:Int){
        viewModelScope.launch {
            local_repository.deleteTvShowFromFavorites(id.toString())
        }
    }


    fun moveFromFavoritesTowatchlist(id:Int){
        viewModelScope.launch {
            local_repository.moveFromFavoritesTowatchlist(id.toString())
        }
    }


    fun moveFromwatchlistToSeen(id:Int){
        viewModelScope.launch {
            local_repository.moveFromwatchlistToSeen(id.toString())
        }
    }


}