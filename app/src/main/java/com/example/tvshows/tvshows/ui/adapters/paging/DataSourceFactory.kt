package com.example.tvshows.tvshows.ui.adapters.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying
import kotlinx.coroutines.CoroutineScope

class DataSourceFactory(var remoteRepository: RemoteRepository, var scope: CoroutineScope, private var query: String ="") : DataSource.Factory<Int, Result_NowPlaying>() {

    var itemLiveDataSource= MutableLiveData<PageKeyedDataSource<Int,Result_NowPlaying>>()

    override fun create(): DataSource<Int, Result_NowPlaying>? {
        val item=ShowsDataSource(remoteRepository,scope,query)
        itemLiveDataSource.postValue(item)
        return item
    }

//    fun getQuery() = query
//
//    fun getSource() = itemLiveDataSource.value

    fun updateQuery(query: String) {
        this.query = query
    }


}