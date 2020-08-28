package com.example.tvshows.ui.watchlist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvshows.TvShowRoomDatabase
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.local_repository
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.utils.Extension_Utils.Companion.warning_toast
import kotlinx.coroutines.async
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

    suspend fun rowExists(id: String, current_fragment: String): Boolean {
        return local_repository.rowExists(id, current_fragment, viewModelScope)
    }

    fun moveToFavorites(obj: TvShowDetails){
        viewModelScope.launch {
            val exists = async { rowExists(obj.id.toString(), "favorites")}.await()
            if (!exists) {
                async {local_repository.deleteTvShowFromWatchlist(obj.id.toString())}.await()
                obj.currentFragment="favorites"
                local_repository.insertTvshowDetailstoDb(obj,viewModelScope)
            }else{
                context.warning_toast("It already exists in seen!")
            }
        }
    }
   fun insert(obj:TvShowDetails){
       viewModelScope.launch {
           local_repository.moveToSeen(obj.id)
       }
   }
    fun moveToSeen(obj: TvShowDetails){
        viewModelScope.launch {
            val exists = rowExists(obj.id.toString(), "seen")
            if (!exists) {
              async {local_repository.deleteTvShowFromWatchlist(obj.id.toString())}.await()
              obj.currentFragment="seen"
              local_repository.insertTvshowDetailstoDb(obj,viewModelScope)

            }else{
                context.warning_toast("It already exists in seen!")
            }
        }
    }
}