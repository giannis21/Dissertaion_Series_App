package com.example.tvshows.ui.mostpopular

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.ui.toprated.TopRatedViewModel


@Suppress("UNCHECKED_CAST")
class TopRatedViewModelFactory(private val remoteRepository: RemoteRepository, var context: Context) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TopRatedViewModel::class.java)) {
            return TopRatedViewModel(remoteRepository,context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}