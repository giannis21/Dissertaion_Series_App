package com.example.tvshows

import androidx.recyclerview.widget.DiffUtil
import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying

class DiffUtilCallback(var oldItemList:MutableList<Result_NowPlaying>,var newItemList:MutableList<Result_NowPlaying>):DiffUtil.Callback(){
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItemList[oldItemPosition].id == newItemList[newItemPosition].id
    }

    override fun getOldListSize(): Int {
        return oldItemList.size
    }

    override fun getNewListSize(): Int {
        return newItemList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItemList[oldItemPosition].equals(newItemList[newItemPosition])
    }

}
