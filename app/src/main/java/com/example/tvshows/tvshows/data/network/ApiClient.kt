package com.example.tvshows.data

import com.example.tvshows.data.network.response.genres.Genres
import com.example.tvshows.data.network.NetworkConnectionIncterceptor
 import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying
import com.example.tvshows.tvshows.data.network.response.Guest_Session
import com.example.tvshows.tvshows.data.network.response.RateResponse
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiClient {

    @GET("genre/tv/list")
    suspend fun getGenres(): Genres

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

//-------------------rate show---------------------------------//s
    @Headers("Content-Type: application/json")
    @POST("tv/{tv_id}/rating")
    suspend fun rateTvShow(@Path("tv_id") id:String,@Query("guest_session_id") guest_session_id:String,@Body rate: JsonObject): RateResponse

    //get guest_session id to rate
    @GET("authentication/guest_session/new")
    suspend fun getguest_session(): Guest_Session

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