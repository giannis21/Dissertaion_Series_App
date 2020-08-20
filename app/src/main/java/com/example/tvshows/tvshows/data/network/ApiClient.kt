package com.example.tvshows.data

import com.example.tvshows.data.network.response.genres.Genres
import com.example.tvshows.data.network.NetworkConnectionIncterceptor
 import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiClient {

    @GET("genre/tv/list")
   suspend fun getGenres(): Genres
   // https://developers.themoviedb.org/3/tv/get-tv-airing-today
   // https://api.themoviedb.org/3/tv/airing_today?api_key=e7f37ba18b2263f1980dfdd25171d0c2&language=en-US&page=1

    @GET("tv/airing_today")
    suspend fun getPlayingNow(@Query("language") lang: String ="en-US", @Query("page") page:String): NowPlaying

    @GET("tv/popular")
    suspend fun getMostPopular(@Query("language") lang:String="en-US", @Query("page") page:String) : NowPlaying

    @GET("search/tv")
    suspend fun searchTvShow(@Query("language") lang: String="en-US", @Query("page") page:String ,@Query("query") query: String): NowPlaying

    @GET("tv/{tv_id}")
    suspend fun getTvShowDetails(@Path("tv_id") id:String, @Query("language") lang: String="en-US" ): TvShowDetails

    @GET("tv/top_rated")
    suspend fun getTopRated(@Query("language") lang: String ="en-US", @Query("page") page:String): NowPlaying



    companion object {

        operator fun invoke(networkConnectionIncterceptor: NetworkConnectionIncterceptor): ApiClient {
            val interceptor = Interceptor { chain ->
                val url = chain.request().url().newBuilder()
                    .addQueryParameter("api_key", "e7f37ba18b2263f1980dfdd25171d0c2").build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
                chain.proceed(request)
            }

            val okHttpClient1 = OkHttpClient.Builder()
                .addInterceptor(networkConnectionIncterceptor)
                .addInterceptor(interceptor)

            return Retrofit.Builder().client(okHttpClient1.build())
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiClient::class.java)
        }
    }

    // https://api.themoviedb.org/3/genre/movie/list?api_key=e7f37ba18b2263f1980dfdd25171d0c2&language=en-US


}