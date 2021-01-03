package com.example.tvshows.data.network.response.details

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "show_details", primaryKeys = ["id", "currentFragment"])
data class TvShowDetails(
    val backdrop_path: String?,
    val created_by: MutableList<CreatedBy>,
    val episode_run_time: MutableList<Int>,
    val first_air_date: String,
    val genres: MutableList<Genre>,
    val homepage: String,
    val id: Int,
    val in_production: Boolean,
    //  val languages: MutableList<String>,
    val last_air_date: String,
    val last_episode_to_air: LastEpisodeToAir?,
    val name: String,
    //  val networks: MutableList<Network>,
    val next_episode_to_air: NextEpisodeToAir?,
    val number_of_episodes: Int,
    val number_of_seasons: Int,
    //  val origin_country: MutableList<String>,
    val original_language: String,
    val original_name: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String?,
    // val production_companies: MutableList<Any>,
    val seasons: MutableList<Season>,
    val status: String,
    val type: String,
    val vote_average: Double,
    val vote_count: Int
) {
    var currentFragment: String = ""
    var underNotification=false
    var exactDateOfNotification : String = ""
}