package com.example.tvshows.ui.show_details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tvshows.data.RemoteRepository


@Suppress("UNCHECKED_CAST")
class DetailsViewModelFactory(private val remoteRepository: RemoteRepository, var context: Context) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShowDetailsViewModel::class.java)) {
            return ShowDetailsViewModel(remoteRepository,context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}