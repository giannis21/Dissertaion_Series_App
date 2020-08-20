package com.example.tvshows.data


import android.content.Context
import androidx.lifecycle.LiveData
import com.example.tvshows.TvShowDao
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying
import com.example.tvshows.tvshows.utils.PreferenceUtils
import com.example.tvshows.utils.Extension_Utils.Companion.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class local_repository(private val tvShowDao: TvShowDao) {


    val now_playing: LiveData<List<NowPlaying>> = tvShowDao.get_now_playing()
    val countOfNowPLaying = tvShowDao.countOfNowPLaying()

    suspend fun get_now_playing_per_page(page: Int): NowPlaying {
        return tvShowDao.get_now_playing_per_page(page)
    }

    fun insertFromAPItoDb(tvShow: NowPlaying, viewModelScope: CoroutineScope) {
        viewModelScope.launch(Dispatchers.IO) {
            tvShowDao.insert(tvShow)
        }
    }


    suspend fun deleteAllFromNowPlaying(viewModelScope: CoroutineScope) {
        viewModelScope.launch(Dispatchers.IO) {
            tvShowDao.deleteAllFromNowPlaying()
        }

    }

    //-----------------------------Most popular methods-----------------------------//
    suspend fun getMostPopularPerPage(page: Int): NowPlaying {
        return tvShowDao.getMostPopularPerPage(page)
    }

    suspend fun deleteAllFromPopular() {
        return tvShowDao.deleteAllFromPopular()
    }

    //-----------------------------Top rated methods-----------------------------//
    suspend fun getTopRated(page: Int): NowPlaying {
        return tvShowDao.getTopRated(page)
    }


    fun insertTvshowDetailstoDb(tvShow: TvShowDetails, viewModelScope: CoroutineScope) {
        viewModelScope.launch(Dispatchers.IO) {
            tvShowDao.insertToTvShowDetails(tvShow)
        }
    }

    //-----------------------------watchlist methods-----------------------------//
    fun getWatchlist(): LiveData<MutableList<TvShowDetails>> {
        return tvShowDao.getWatchlistShows()
    }

    suspend fun getTvShowDetails(id: String): TvShowDetails {
        return tvShowDao.getTvShowDetails(id)
    }

    suspend fun deleteTvShowFromWatchlist(id: String) {
        tvShowDao.deleteTvShowFromWatchlist(id)
    }

    suspend fun moveFromwatchlistToFavorites(id: String) {
        tvShowDao.moveFromwatchlistToFavorites(id)
    }

    suspend fun moveFromwatchlistToSeen(id: String) {
        tvShowDao.moveFromwatchlistToSeen(id)
    }

    fun countTvShowsFromWatchlist(): LiveData<Int> {
        return tvShowDao.countTvShowsFromWatchlist()
    }

    //------------------favorites methods--------------------------//
    fun countTvShowsFromFavorites(): LiveData<Int> {
        return tvShowDao.countTvShowsFromFavorites()
    }

    fun getFavorites(): LiveData<MutableList<TvShowDetails>> {
        return tvShowDao.getFavorites()
    }
    suspend fun deleteTvShowFromFavorites(id: String) {
        tvShowDao.deleteTvShowFromFavorites(id)
    }

    suspend fun moveFromFavoritesTowatchlist(id: String) {
        tvShowDao.moveFromFavoritesTowatchlist(id)
    }


    fun fetchNeeded(context: Context): Boolean {

        val calendar = Calendar.getInstance()
        val timeInMilli = calendar.timeInMillis
        val minutes = timeInMilli / (60 * 1000)
        val lastTime = PreferenceUtils.getLastTime(context).toInt()

        if (minutes - lastTime > 60 * 3) {
            PreferenceUtils.setLastTime(minutes.toString(), context)
            return true
        }
        return false
    }

}
