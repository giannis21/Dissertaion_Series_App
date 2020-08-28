package com.example.tvshows.ui.seen

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

class SeenViewModel(remoteRepository: RemoteRepository,var  context: Context) : ViewModel() {

    val details: LiveData<MutableList<TvShowDetails>>
    private var local_repository: local_repository
    var countOfseen: LiveData<Int>

    init {
        val TvshowsDao = TvShowRoomDatabase.getDatabase(context).tvShowDao()
        local_repository = local_repository(TvshowsDao)
        details = local_repository.getSeen()
        countOfseen = local_repository.countTvShowsFromSeen()
    }

    fun deleteTvshow(id: Int) {
        viewModelScope.launch {
            local_repository.deleteTvShowFromSeen(id.toString())
        }
    }

    suspend fun rowExists(id: String, current_fragment: String): Boolean {
        return local_repository.rowExists(id, current_fragment, viewModelScope)
    }

    fun moveToWatchlist(obj: TvShowDetails) {
        viewModelScope.launch {
            val exists = rowExists(obj.id.toString(), "watchlist")
            if (!exists) {
                async {local_repository.deleteTvShowFromSeen(obj.id.toString())}.await()
                obj.currentFragment="watchlist"
                local_repository.insertTvshowDetailstoDb(obj,viewModelScope)
            }else{
                context.warning_toast("It already exists in watchlist!")
            }
        }
    }

    fun moveToFavorites(obj: TvShowDetails) {
        viewModelScope.launch {
            val exists = rowExists(obj.id.toString(), "favorites")
            if (!exists) {
                async {local_repository.deleteTvShowFromSeen(obj.id.toString())}.await()
                obj.currentFragment="favorites"
                local_repository.insertTvshowDetailstoDb(obj,viewModelScope)

            }else{
                context.warning_toast("It already exists in watchlist!")
            }
        }
    }


}