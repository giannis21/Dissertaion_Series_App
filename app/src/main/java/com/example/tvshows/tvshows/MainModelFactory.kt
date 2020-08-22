package com.example.tvshows.ui.mostpopular

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.ui.seen.MainViewModel
import com.example.tvshows.ui.watchlist.WatchlistViewModel


@Suppress("UNCHECKED_CAST")
class MainModelFactory(var context: Context) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}