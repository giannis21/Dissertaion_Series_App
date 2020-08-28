package com.example.tvshows.data


import android.util.Log
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.data.network.response.genres.Genres
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying
import com.example.tvshows.tvshows.data.network.response.Guest_Session
import com.example.tvshows.tvshows.data.network.response.RateResponse
import com.google.gson.JsonObject
import java.lang.Exception

class RemoteRepository(private val my_Api: ApiClient) {

    suspend fun fetch_genres(): Genres {
        return my_Api.getGenres()
    }
    suspend fun searchTvShows(page:Int,query:String): NowPlaying {
        return my_Api.searchTvShow("en_US",page.toString(),query)
    }

    suspend fun getMostPopular(page:Int): NowPlaying {
        return my_Api.getMostPopular("en_US",page.toString())
    }
    suspend fun fetch_now_playing(page:Int): NowPlaying {
        return my_Api.getPlayingNow("en_US",page.toString())
    }
    suspend fun getTvShowDetails(id:String): TvShowDetails {
        return my_Api.getTvShowDetails(id,"en_US")
    }

    suspend fun getTopRated(page:Int): NowPlaying {
        return my_Api.getTopRated("en_US",page.toString())
    }

    suspend fun rateTvShow(rate: String, guestSessionId:String, obj: JsonObject):RateResponse {
        return my_Api.rateTvShow(rate,guestSessionId,obj)
    }
    suspend fun getguest_session():Guest_Session {
        return my_Api.getguest_session()
    }

}