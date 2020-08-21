package com.example.tvshows.ui.seen

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvshows.TvShowRoomDatabase
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.local_repository
import com.example.tvshows.data.network.response.details.TvShowDetails
import kotlinx.coroutines.launch

class SeenViewModel(remoteRepository: RemoteRepository, context: Context) : ViewModel() {

    val details: LiveData<MutableList<TvShowDetails>>
    private var local_repository: local_repository
    var countOfseen: LiveData<Int>

    init {
        val TvshowsDao = TvShowRoomDatabase.getDatabase(context).tvShowDao()
        local_repository = local_repository(TvshowsDao)
        details=local_repository.getSeen()
        countOfseen=local_repository.countTvShowsFromSeen()
    }

    fun deleteTvshow(id:Int){
        viewModelScope.launch {
            local_repository.deleteTvShowFromSeen(id.toString())
        }
    }


    fun moveFromSeenToWatchlist(id:Int){
        viewModelScope.launch {
            local_repository.moveFromSeenToWatchlist(id.toString())
        }
    }
    fun  moveFromSeenToFavorites(id:Int){
        viewModelScope.launch {
            local_repository.moveFromSeenToFavorites(id.toString())
        }
    }


}