package com.example.tvshows.tvshows

import android.annotation.SuppressLint
import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying
import com.example.tvshows.ui.nowplaying.NowPlayingFragment
import com.example.tvshows.ui.seen.MainViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object SharedMethods {

    @SuppressLint("SimpleDateFormat")
    fun getDateInMilli(releaseDate: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd")

        return try {
            val mDate: Date = sdf.parse(releaseDate)
            mDate.getTime()
        } catch (e: ParseException) {
            0
        }
    }

     fun filterList(list_allGenres:MutableList<Result_NowPlaying>):MutableList<Result_NowPlaying> {
        val tempList_filtered= mutableListOf<Result_NowPlaying>()
        tempList_filtered.clear()
        for(i in 0 until list_allGenres.size){
             list_allGenres[i].genre_ids.forEach{ current_genre->
                for (selectedGenre in MainViewModel.selected_genres) {
                    if(selectedGenre.id == current_genre) {
                        tempList_filtered.add(list_allGenres[i])
                        break
                    }
                }
            }
        }
        return tempList_filtered
    }
}