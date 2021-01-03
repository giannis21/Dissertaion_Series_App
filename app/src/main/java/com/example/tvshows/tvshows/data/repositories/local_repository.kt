package com.example.tvshows.data


import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Query
import com.example.tvshows.TvShowDao
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying
import com.example.tvshows.tvshows.utils.PreferenceUtils
import kotlinx.coroutines.*
import timber.log.Timber
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




    //-----------------------------Most popular methods-----------------------------//
    suspend fun getMostPopularPerPage(page: Int): NowPlaying {
        return tvShowDao.getMostPopularPerPage(page)
    }

    suspend fun deleteAllFromMostPopular(viewModelScope: CoroutineScope) {
        viewModelScope.launch(Dispatchers.IO) {
            tvShowDao.deleteAllFromPopular()
        }
    }
    //-----------------------------Top rated methods-----------------------------//
    suspend fun getTopRated(page: Int): NowPlaying {
        return tvShowDao.getTopRated(page)
    }


    fun insertTvshowDetailstoDb(tvShow: TvShowDetails, viewModelScope: CoroutineScope): Job {
        var supervisorJob1 = SupervisorJob()
        return viewModelScope.launch {
            runCatching {
                tvShowDao.insertToTvShowDetails(tvShow)
            }.onFailure {
                Timber.e("ssssssss failed $it")
            }.onSuccess {
                Timber.e("ssssssss insert success $it")
            }

        }
    }
    suspend fun deleteAllFromTop(viewModelScope: CoroutineScope) {
        viewModelScope.launch(Dispatchers.IO) {
            tvShowDao.deleteAllFromTop()
        }
    }


    //-----------------------------watchlist methods-----------------------------//
    fun getWatchlist(): LiveData<MutableList<TvShowDetails>> {
        return tvShowDao.getWatchlistShows()
    }

    suspend fun getTvShowDetails(id: String): TvShowDetails {
        return tvShowDao.getTvShowDetails(id)
    }

     fun getTvShowDetailsAll(): LiveData<MutableList<TvShowDetails>> {
        return tvShowDao.getTvShowDetailsAll()
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

    suspend fun rowExists(id: String, currentFragment: String, viewModelScope: CoroutineScope): Boolean { return tvShowDao.RowExists(id, currentFragment) }


    fun fetchNeeded(context: Context): Boolean {

        val calendar = Calendar.getInstance()
        val timeInMilli = calendar.timeInMillis
        val minutes = timeInMilli / (60 * 1000)
        val lastTime = PreferenceUtils.getLastTime(context).toInt()

        if (minutes - lastTime > 60) {
            PreferenceUtils.setLastTime(minutes.toString(), context)
            println("SSSSSSS  $minutes  $lastTime --- ${minutes-lastTime}")
            return true
        }
        return false
    }

    suspend fun underNotification(id: String, b: Boolean,release_date:String) {
        try {
             tvShowDao.underNotification(id ,b)
        } catch (e: Exception) {
           Timber.e(e)
        }
    }

    suspend fun update_exact_time_of_notification(id: String, date: String)  {
        try {
            tvShowDao.update_exact_time_of_notification(id ,date)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }


}
