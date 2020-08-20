package com.example.tvshows.ui.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tvshows.data.RemoteRepository


@Suppress("UNCHECKED_CAST")
class SearchViewModelFactory(private val remoteRepository: RemoteRepository, var context: Context  ) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(remoteRepository,context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}