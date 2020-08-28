package com.example.tvshows.tvshows

import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying
import com.example.tvshows.ui.nowplaying.NowPlayingFragment
import com.example.tvshows.ui.seen.MainViewModel

object SharedMethods {


     fun filterList(list_allGenres:MutableList<Result_NowPlaying>):MutableList<Result_NowPlaying> {
        val tempList_filtered= mutableListOf<Result_NowPlaying>()
        tempList_filtered.clear()
        for(i in 0..list_allGenres.size-1){
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