package com.example.tvshows.data.network.response.nowPlaying

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.NotNull

@Entity(tableName = "now_playing")
data class NowPlaying(
    @PrimaryKey(autoGenerate = true)
    val generalid: Long,
    val page: Int,
    @SerializedName("results")
    val resultNowPlayings: MutableList<Result_NowPlaying>,
    val total_pages: Int,
    val total_results: Int
){
    var currentFragment:String=""
}