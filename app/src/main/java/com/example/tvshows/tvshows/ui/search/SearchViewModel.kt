package com.example.tvshows.ui.search

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying
import com.example.tvshows.utils.Extension_Utils.Companion.toast
import kotlinx.coroutines.launch

class SearchViewModel(private val remoteRepository: RemoteRepository, var context: Context) : ViewModel() {
    var searched = MutableLiveData<NowPlaying>()

    fun searchTvShows(page: Int, query: String) {
        viewModelScope.launch {
            try {
                searched.value=remoteRepository.searchTvShows(page, query)
                context.toast(remoteRepository.searchTvShows(page, query).resultNowPlayings.size.toString())
            } catch (ex: Exception) {
                context.toast(ex.message.toString())

            }
        }
    }



}