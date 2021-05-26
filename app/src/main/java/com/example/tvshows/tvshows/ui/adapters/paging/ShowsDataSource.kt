package com.example.tvshows.tvshows.ui.adapters.paging

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.tvshows.data.RemoteRepository
import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying
import kotlinx.coroutines.*

class ShowsDataSource(var remoteRepository: RemoteRepository, private var scope: CoroutineScope, var query: String) :
    PageKeyedDataSource<Int, Result_NowPlaying>() {

    companion object{
        var firstResults: ((Int) -> Unit)? =null
    }
    var FirstPage = 1
    private var supervisorJob = SupervisorJob()
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Result_NowPlaying>) {
        if(query.isNotEmpty())
        {
            scope.launch(supervisorJob) {
                try {
                    val response = remoteRepository.searchTvShows(FirstPage,query)
                    response.resultNowPlayings.let {
                        firstResults?.invoke(it.size)
                        callback.onResult(it, null, (FirstPage + 1))
                    }
                } catch (exception: Exception) {}
            }
        }else{
            firstResults?.invoke(0)
        }


    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Result_NowPlaying>) {
        scope.launch(supervisorJob)  {
            try {
                val key = if(params.key.toInt() > 1 ){ params.key.toInt() -1 }else null
                val response = remoteRepository.searchTvShows(key!!.toInt(),query)
                response.resultNowPlayings.let {
                   callback.onResult(it,key)
                }
            } catch (exception: Exception) {}

        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Result_NowPlaying>) {
        scope.launch(supervisorJob)  {
            try {
                val response = remoteRepository.searchTvShows(params.key,query)
                val key =  params.key.toInt() + 1
                response.resultNowPlayings.let {
                    callback.onResult(it,key)
                }
            } catch (exception: Exception) {}
        }
    }
    override fun invalidate() {
        super.invalidate()
        supervisorJob.cancelChildren()
    }
}