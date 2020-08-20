package com.example.tvshows.ui.mostpopular

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvshows.TvShowRoomDatabase
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.local_repository
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying
import com.example.tvshows.ui.mostpopular.MostPopularFragment.Companion.pages_counterP
import com.example.tvshows.utils.Extension_Utils.Companion.toast
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MostPopularViewModel(var remoteRepository: RemoteRepository, var context: Context) : ViewModel() {

    var popular = MutableLiveData<NowPlaying>()
    private var local_repository: local_repository
     var countOfNowPLaying: LiveData<Int>
    init {
        val TvshowsDao = TvShowRoomDatabase.getDatabase(context).tvShowDao()
        local_repository = local_repository(TvshowsDao)
        countOfNowPLaying=local_repository.countOfNowPLaying
    }

    fun getMostPopularPerPage(page: Int) {
        viewModelScope.launch {
            try {
               // if (pages_counterP == 1 && netMethods.hasInternet(context, 0) && local_repository.fetchNeeded(context))
                 //   local_repository.deleteAllFromNowPlaying(viewModelScope)

                val result = async { local_repository.getMostPopularPerPage(page) }.await()
                if (result == null)
                    fetchMostPopularFromApi(page)
                else {
                    if (page <= result.resultNowPlayings.size) {
                        result.currentFragment = "mostPopular"
                        popular.value = result
                    }
                }
            } catch (ex: Exception) {
                context.toast(ex.message.toString())
            }
        }
    }


    fun fetchMostPopularFromApi(page: Int) {
        viewModelScope.launch {
            try {
                val obj = remoteRepository.getMostPopular(page)
                obj.currentFragment = "mostPopular"
                popular.value = obj

                if (page <= obj.resultNowPlayings.size)
                    local_repository.insertFromAPItoDb(obj, viewModelScope)

            } catch (ex: Exception) {
                context.toast(ex.message.toString())
                pages_counterP--
            }
        }
    }



}