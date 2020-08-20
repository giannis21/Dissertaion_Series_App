package com.example.tvshows.ui.nowplaying

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvshows.TvShowRoomDatabase
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.local_repository
import com.example.tvshows.data.netMethods
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying
import com.example.tvshows.ui.nowplaying.NowPlayingFragment.Companion.pages_counter
import com.example.tvshows.utils.Extension_Utils.Companion.toast
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class NowPlayingViewModel(private val remoteRepository: RemoteRepository, var context: Context) : ViewModel() {

    private var local_repository: local_repository
    var allPlayingNowInDb: LiveData<List<NowPlaying>>
    var playingNowDb_ = MutableLiveData<NowPlaying>()
    var countOfNowPLaying: LiveData<Int>

    init {
        val TvshowsDao = TvShowRoomDatabase.getDatabase(context).tvShowDao()
        local_repository = local_repository(TvshowsDao)
        allPlayingNowInDb = local_repository.now_playing
        countOfNowPLaying = local_repository.countOfNowPLaying
    }


    fun get_now_playing_per_page(page: Int) {
        viewModelScope.launch {
            try {

                //καθαρίζω τον πίνακα now_playing της βάσης δεδομένων μόνο αν έχει περάσει 1 ώρα && βρίσκομαι στην 1η σελίδα && είμαι ενεργός στο ίντερνετ
                if (pages_counter == 1 && netMethods.hasInternet(context, 0) && local_repository.fetchNeeded(context))
                    local_repository.deleteAllFromNowPlaying(viewModelScope)

                val result = async { local_repository.get_now_playing_per_page(page) }.await()

                if (result == null)
                    fetchNowPlayingFromApi(page)
                else {
                    if (page <= result.resultNowPlayings.size) {
                        result.currentFragment = "nowPlaying"
                        playingNowDb_.value = result
                    }
                }
            } catch (ex: Exception) {
                context.toast(ex.message.toString())
                pages_counter--
            }

        }
    }

    fun fetchNowPlayingFromApi(page: Int) {
        viewModelScope.launch {
            try {
                val obj = remoteRepository.fetch_now_playing(page)
                obj.currentFragment = "nowPlaying"
                playingNowDb_.value = obj

                if (page <= obj.resultNowPlayings.size)
                    local_repository.insertFromAPItoDb(obj, viewModelScope)

            } catch (ex: Exception) {
                context.toast(ex.message.toString())
                pages_counter--
            }
        }
    }

}
