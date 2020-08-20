package com.example.tvshows.ui.toprated

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvshows.TvShowRoomDatabase
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.local_repository
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying
import com.example.tvshows.tvshows.ui.callbacks.showConnectivityError
import com.example.tvshows.ui.toprated.TopRatedFragment.Companion.pages_counterTopRated
import com.example.tvshows.utils.Extension_Utils.Companion.toast
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TopRatedViewModel(private val remoteRepository: RemoteRepository, var context: Context) : ViewModel() {
    var topRated = MutableLiveData<NowPlaying>()

    private var local_repository: local_repository
     val countOfNowPLaying:LiveData<Int>
    init {
        val TvshowsDao = TvShowRoomDatabase.getDatabase(context).tvShowDao()
        local_repository = local_repository(TvshowsDao)
        countOfNowPLaying=local_repository.countOfNowPLaying
    }

    fun getTopRatedPerPage(page: Int) {
        viewModelScope.launch {
            try {
               // if(pages_counterTopRated == 1 && netMethods.hasInternet(context,0) && local_repository.fetchNeeded(context))
                   // deleteAll()

                val result = async { local_repository.getTopRated(page) }.await()

                if (result==null)
                    fetchTopRatedFromApi(page)
                else {
                    if (page <= result.resultNowPlayings.size) {
                        result.currentFragment = "topRated"
                        topRated.value = result
                    }
                }
            } catch (ex: Exception) {
                context.toast(ex.message.toString())

            }
        }
    }

    fun fetchTopRatedFromApi(page: Int) {
        viewModelScope.launch {
            try {
                val obj = remoteRepository.getTopRated(page)
                obj.currentFragment="topRated"
                topRated.value = obj

                if (page <= obj.resultNowPlayings.size)
                    local_repository.insertFromAPItoDb(obj,viewModelScope)

            } catch (ex: Exception) {
                context.toast(ex.message.toString())
                pages_counterTopRated--
            }
        }
    }




}