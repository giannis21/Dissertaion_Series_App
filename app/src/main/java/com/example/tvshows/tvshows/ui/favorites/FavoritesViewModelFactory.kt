package com.example.tvshows.ui.nowplaying

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.ui.favorites.FavoritesViewModel


@Suppress("UNCHECKED_CAST")
class FavoritesViewModelFactory(private val remoteRepository: RemoteRepository, var context: Context) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            return FavoritesViewModel(remoteRepository,context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}