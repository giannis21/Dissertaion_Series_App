package com.example.tvshows.ui.search

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying
import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying
import com.example.tvshows.tvshows.ui.adapters.paging.DataSourceFactory
import com.example.tvshows.utils.Extension_Utils.Companion.error_toast
import kotlinx.coroutines.*

class SearchViewModel(private val remoteRepository: RemoteRepository, var context: Context) :
    ViewModel() {
    val ioScope = CoroutineScope(Dispatchers.Default)
    val config = PagedList.Config.Builder()
        .setPageSize(20)
        .setEnablePlaceholders(false)
        .build()

     var factory = DataSourceFactory(remoteRepository, ioScope)
     var itemPagedList = LivePagedListBuilder<Int, Result_NowPlaying>(factory, config).build()



    fun searchTvShows1(query: String) {
        val search = query.trim()
        factory.updateQuery(search)
    }

    fun reserfactory(){
        factory.updateQuery("")
    }
    override fun onCleared() {
        super.onCleared()

        ioScope.coroutineContext.cancel()
    }

    fun searchTvShows(page: Int, query: String, callback: ((NowPlaying) -> Unit)) {
        viewModelScope.launch {
            try {
                callback.invoke(remoteRepository.searchTvShows(page, query))
            } catch (ex: Exception) {
                context.error_toast(ex.message.toString())
            }
        }
    }

}