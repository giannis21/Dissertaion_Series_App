package com.example.tvshows.ui.nowplaying

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tvshows.data.RemoteRepository


@Suppress("UNCHECKED_CAST")
class NowPlayingViewModelFactory(private val remoteRepository: RemoteRepository, var context: Context) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NowPlayingViewModel::class.java)) {
            return NowPlayingViewModel(remoteRepository,context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}