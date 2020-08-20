package com.example.tvshows

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying

@Dao
interface TvShowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(nowPlaying: NowPlaying)

    @Query("SELECT * from now_playing")
    fun get_now_playing(): LiveData<List<NowPlaying>>

    @Query("SELECT * FROM now_playing where page = :page AND currentFragment='nowPlaying'")
    suspend fun get_now_playing_per_page(page: Int): NowPlaying


    @Query("SELECT count(*) from now_playing")
    fun isDbEmpty(): Int

    @Query("DELETE FROM now_playing ")
    suspend fun deleteAllFromNowPlaying()

    @Query("SELECT COUNT(page) from now_playing  ")
    fun countOfNowPLaying(): LiveData<Int>



//---------------------------------Most Popular queries--------------------------------//

    @Query("SELECT * FROM now_playing where page = :page AND currentFragment='mostPopular' ")
    suspend fun getMostPopularPerPage(page: Int): NowPlaying

    @Query("DELETE FROM now_playing Where currentFragment='mostPopular'")
    suspend fun deleteAllFromPopular()

//---------------------------------Top rated queries----------------------------------------//

    @Query("SELECT * FROM now_playing where page = :page AND currentFragment='topRated'")
    suspend fun getTopRated(page: Int): NowPlaying

//----------------------------------Watchlist queries------------------------------------//

    @Query("SELECT * FROM show_details where currentFragment='watchlist'")
    fun getWatchlistShows(): LiveData<MutableList<TvShowDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToTvShowDetails(show_details: TvShowDetails)

    @Query("SELECT * FROM show_details where id=:id")
    suspend fun getTvShowDetails(id: String): TvShowDetails

    @Query("DELETE FROM show_details Where id=:id AND currentFragment='watchlist'")
    suspend fun deleteTvShowFromWatchlist(id: String)

    // move from watchlist to favorites
    @Query("UPDATE show_details SET  currentFragment='favorites' where id=:id")
    suspend fun moveFromwatchlistToFavorites(id: String)

    @Query("UPDATE show_details SET  currentFragment='seen' where id=:id")
    suspend fun moveFromwatchlistToSeen(id: String)

    @Query("select COUNT(id)  from show_details Where currentFragment='watchlist' ")
    fun countTvShowsFromWatchlist(): LiveData<Int>

    //----------------------favorites------------------------------------//

    @Query("select COUNT(id)  from show_details Where currentFragment='favorites' ")
    fun countTvShowsFromFavorites(): LiveData<Int>

    @Query("SELECT * FROM show_details where currentFragment='favorites'")
    fun getFavorites(): LiveData<MutableList<TvShowDetails>>

    @Query("DELETE FROM show_details Where id=:id AND currentFragment='favorites'")
    suspend fun deleteTvShowFromFavorites(id: String)

    @Query("UPDATE show_details SET  currentFragment='watchlist' where id=:id")
    suspend fun moveFromFavoritesTowatchlist(id: String)
}