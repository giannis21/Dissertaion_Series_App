package com.example.tvshows.data


import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.data.network.response.genres.Genres
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying

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
}