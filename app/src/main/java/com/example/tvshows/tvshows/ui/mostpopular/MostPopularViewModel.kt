package com.example.tvshows.ui.mostpopular

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvshows.MainActivity
import com.example.tvshows.TvShowRoomDatabase
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.local_repository
import com.example.tvshows.data.netMethods
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying
import com.example.tvshows.ui.mostpopular.MostPopularFragment.Companion.pages_counterP
import com.example.tvshows.ui.nowplaying.NowPlayingFragment
import com.example.tvshows.utils.Extension_Utils.Companion.error_toast
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MostPopularViewModel(var remoteRepository: RemoteRepository, var context: Context) :
    ViewModel() {

    var popular = MutableLiveData<NowPlaying>()
    private var local_repository: local_repository
    var connectivityProblem = MutableLiveData<Boolean>(false)
    var countOfNowPLaying: LiveData<Int>
    var total_pages = 2

    init {
        val TvshowsDao = TvShowRoomDatabase.getDatabase(context).tvShowDao()
        local_repository = local_repository(TvshowsDao)
        countOfNowPLaying = local_repository.countOfNowPLaying
    }

    fun getMostPopularPerPage(page: Int) {
        if (page == 1)
            MainActivity.showProgressBar.invoke(true)

        viewModelScope.launch {
            try {
                if (pages_counterP == 1 && netMethods.hasInternet(context, true) && local_repository.fetchNeeded(context))
                    local_repository.deleteAllFromMostPopular(viewModelScope)

                val result = async { local_repository.getMostPopularPerPage(page) }.await()
                if (result == null)
                    fetchMostPopularFromApi(page)
                else {
                    if (page <= total_pages) {
                        MainActivity.showProgressBar.invoke(false)
                        result.currentFragment = "mostPopular"
                        popular.value = result
                        total_pages = result.total_pages
                    }
                }
            } catch (ex: Exception) {
                context.error_toast(ex.message.toString())
                MainActivity.showProgressBar.invoke(false)
                if (pages_counterP == 1)
                    connectivityProblem.postValue(true)
            }
        }
    }


    fun fetchMostPopularFromApi(page: Int) {
        viewModelScope.launch {
            try {
                val obj = remoteRepository.getMostPopular(page)
                obj.currentFragment = "mostPopular"
                popular.value = obj

                if (page <= total_pages) {
                    local_repository.insertFromAPItoDb(obj, viewModelScope)
                    total_pages = obj.total_pages
                    MainActivity.showProgressBar.invoke(false)
                }
            } catch (ex: Exception) {
                context.error_toast(ex.message.toString())
                MainActivity.showProgressBar.invoke(false)
                if (pages_counterP == 1)
                    connectivityProblem.postValue(true)
                pages_counterP--
            }
        }
    }


}