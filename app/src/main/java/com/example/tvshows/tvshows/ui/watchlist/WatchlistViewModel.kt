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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class WatchlistViewModel(remoteRepository: RemoteRepository, var context: Context) : ViewModel() {

    val details: LiveData<MutableList<TvShowDetails>>
    private var local_repository: local_repository
    var countOfWatchlist: LiveData<Int>

    init {
        val TvshowsDao = TvShowRoomDatabase.getDatabase(context).tvShowDao()
        local_repository = local_repository(TvshowsDao)
        details = local_repository.getWatchlist()
        countOfWatchlist = local_repository.countTvShowsFromWatchlist()
    }

    fun deleteTvshow(id: Int) {
        viewModelScope.launch {
            local_repository.deleteTvShowFromWatchlist(id.toString())
        }
    }

    suspend fun rowExists(id: String, current_fragment: String): Boolean {
        return local_repository.rowExists(id, current_fragment, viewModelScope)
    }

    fun moveToFavorites(obj: TvShowDetails) {
        viewModelScope.launch {
            val exists = async { rowExists(obj.id.toString(), "favorites") }.await()
            if (!exists) {
                async { local_repository.deleteTvShowFromWatchlist(obj.id.toString()) }.await()
                obj.currentFragment = "favorites"
                local_repository.insertTvshowDetailstoDb(obj, viewModelScope)
            } else {
                context.warning_toast("It already exists in seen!")
            }
        }
    }

    fun moveToSeen(obj: TvShowDetails) {
        viewModelScope.launch {
            val exists = rowExists(obj.id.toString(), "seen")
            if (!exists) {
                async { local_repository.deleteTvShowFromWatchlist(obj.id.toString()) }.await()
                obj.currentFragment = "seen"
                local_repository.insertTvshowDetailstoDb(obj, viewModelScope)
            } else {
                context.warning_toast("It already exists in seen!")
            }
        }
    }

    fun underNotification(id: Int, b: Boolean, release_date: String) {

        viewModelScope.launch(Dispatchers.Default) {
            kotlin.runCatching {
                local_repository.underNotification(id.toString(), b, release_date)
            }.onFailure {
                Timber.e(it)
            }.onSuccess { response ->
                 Timber.e(response.toString())
            }
        }
    }

    fun update_exact_time_of_notification(id: Int, dateInMilli: String) {
        viewModelScope.launch(Dispatchers.Default) {
            kotlin.runCatching {
                local_repository.update_exact_time_of_notification(id.toString(),dateInMilli)
            }.onFailure {
                Timber.e(it)
            }.onSuccess { response ->
                Timber.e(response.toString())
            }
        }
    }
}