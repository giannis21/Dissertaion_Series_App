package com.example.tvshows

import androidx.room.TypeConverter
import com.example.tvshows.data.network.response.details.*
import com.example.tvshows.data.network.response.nowPlaying.Result_NowPlaying
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {
    @TypeConverter
    fun fromStringToList(value: String?): MutableList<Result_NowPlaying?>? {
        val listType = object : TypeToken<MutableList<Result_NowPlaying?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListtoString(list: MutableList<Result_NowPlaying?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
    //---------------------------Season------------------------------//
    @TypeConverter
    fun fromStringToList3(value: String?): MutableList<Season?>? {
        val listType = object : TypeToken<MutableList<Season>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListtoString3(list: MutableList<Season?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
//-----------------------------created by----------------------------------//

    @TypeConverter
    fun fromStringToList4(value: String): MutableList<CreatedBy?>? {
        val listType = object : TypeToken<MutableList<CreatedBy?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListtoString4(list: MutableList<CreatedBy?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

    //-----------------------------Genres----------------------------------//

    @TypeConverter
    fun fromStringToList5(value: String?): MutableList<Genre?>? {
        val listType = object : TypeToken<MutableList<Genre?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListtoString5(list: MutableList<Genre?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

    //-----------------------------int----------------------------------//

    @TypeConverter
    fun fromStringToList6(value: String?): MutableList<Int?>? {
        val listType = object : TypeToken<MutableList<Int?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListtoString6(list: MutableList<Int?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }


    @TypeConverter
    fun fromStringToList1(value: String?): LastEpisodeToAir? {
        val listType = object : TypeToken<LastEpisodeToAir?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListtoStrin1g(list: LastEpisodeToAir?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
    @TypeConverter
    fun fromStringToList12(value: String?): NextEpisodeToAir? {
        val listType = object : TypeToken<NextEpisodeToAir?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListtoStrin21g(list: NextEpisodeToAir?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

}