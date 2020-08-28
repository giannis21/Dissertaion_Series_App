package com.example.tvshows.ui.favorites

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

class FavoritesViewModel(remoteRepository: RemoteRepository, var context: Context) : ViewModel() {

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

    suspend fun rowExists(id: String, current_fragment: String): Boolean {
        return local_repository.rowExists(id, current_fragment, viewModelScope)
    }

    fun moveTowatchlist(obj: TvShowDetails){
        viewModelScope.launch {
            val exists = rowExists(obj.id.toString(), "watchlist")
            if (!exists) {

                async {local_repository.deleteTvShowFromFavorites(obj.id.toString())}.await()
                obj.currentFragment="watchlist"
                local_repository.insertTvshowDetailstoDb(obj,viewModelScope)
            } else {
                context.warning_toast("It already exists in watchlist!")
            }
        }
    }


    fun moveToSeen(obj: TvShowDetails){
        viewModelScope.launch {
            val exists = rowExists(obj.id.toString(), "seen")
            if (!exists) {
                async {local_repository.deleteTvShowFromFavorites(obj.id.toString())}.await()
                obj.currentFragment="seen"
                local_repository.insertTvshowDetailstoDb(obj,viewModelScope)
            } else {
                context.warning_toast("It already exists in seen!")
            }
        }
    }


}