package com.example.tvshows.data


import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.tvshows.TvShowDao
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying
import com.example.tvshows.tvshows.utils.PreferenceUtils
import kotlinx.coroutines.*
import java.lang.Exception
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


    fun insertTvshowDetailstoDb(tvShow: TvShowDetails, viewModelScope: CoroutineScope): Job {
        return viewModelScope.launch(Dispatchers.IO) {
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


    suspend fun moveToSeen(id: Int) {
        try {
            tvShowDao.moveToSeen(id.toString())
        } catch (e: Exception) {
            Log.i("aaaaa", e.message.toString())
        }

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


    //-----------Seen methods-------------------//
    fun getSeen(): LiveData<MutableList<TvShowDetails>> {
        return tvShowDao.getSeen()
    }


    fun countTvShowsFromSeen(): LiveData<Int> {
        return tvShowDao.countTvShowsFromSeen()
    }

    suspend fun moveToFavorites(id: Int) {
        try {
            tvShowDao.moveToFavorites(id.toString())
        } catch (e: Exception) {
            Log.i("aaaaa", e.message.toString())
        }
    }

    suspend fun moveToWatchlist(id: Int) {
        try {
            tvShowDao.moveToWatchlist(id.toString())
        } catch (e: Exception) {
            Log.i("aaaaa", e.message.toString())
        }

    }

    suspend fun deleteTvShowFromSeen(id: String) {
        tvShowDao.deleteTvShowFromSeen(id)
    }

    //------------------------DEtails----------------------------------//

    suspend fun rowExists(
        id: String,
        currentFragment: String,
        viewModelScope: CoroutineScope
    ): Boolean {
        return tvShowDao.RowExists(id, currentFragment)
    }





    fun fetchNeeded(context: Context): Boolean {

        val calendar = Calendar.getInstance()
        val timeInMilli = calendar.timeInMillis
        val minutes = timeInMilli / (60 * 1000)
        val lastTime = PreferenceUtils.getLastTime(context).toInt()

        if (minutes - lastTime > 0) {
            PreferenceUtils.setLastTime(minutes.toString(), context)
            return true
        }
        return false
    }



}
