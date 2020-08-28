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
import com.example.tvshows.utils.Extension_Utils.Companion.error_toast
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TopRatedViewModel(private val remoteRepository: RemoteRepository, var context: Context) : ViewModel() {

    var topRated = MutableLiveData<NowPlaying>()
    private var local_repository: local_repository
    val countOfNowPLaying: LiveData<Int>
    var total_pages = 2

    init {
        val TvshowsDao = TvShowRoomDatabase.getDatabase(context).tvShowDao()
        local_repository = local_repository(TvshowsDao)
        countOfNowPLaying = local_repository.countOfNowPLaying
    }

    fun getTopRatedPerPage(page: Int) {
        viewModelScope.launch {
            try {
                // if(pages_counterTopRated == 1 && netMethods.hasInternet(context,0) && local_repository.fetchNeeded(context))
                // deleteAll()

                val result = async { local_repository.getTopRated(page) }.await()

                if (result == null)
                    fetchTopRatedFromApi(page)
                else {
                    if (page <= total_pages) {
                        result.currentFragment = "topRated"
                        topRated.value = result
                        total_pages = result.total_pages
                    }
                }
            } catch (ex: Exception) {
                context.error_toast(ex.message.toString())

            }
        }
    }

    fun fetchTopRatedFromApi(page: Int) {
        viewModelScope.launch {
            try {
                val obj = remoteRepository.getTopRated(page)
                obj.currentFragment = "topRated"
                topRated.value = obj

                if (page <= total_pages) {
                    local_repository.insertFromAPItoDb(obj, viewModelScope)
                    total_pages=obj.total_pages
                }
            } catch (ex: Exception) {
                context.error_toast(ex.message.toString())
                pages_counterTopRated--
            }
        }
    }

}