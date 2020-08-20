package com.example.tvshows

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tvshows.data.network.response.details.TvShowDetails
import com.example.tvshows.data.network.response.nowPlaying.NowPlaying


@Database(entities = arrayOf(NowPlaying::class,TvShowDetails::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
public abstract class TvShowRoomDatabase : RoomDatabase() {

    abstract fun tvShowDao(): TvShowDao

    companion object {

        @Volatile
        private var INSTANCE: TvShowRoomDatabase? = null

        fun getDatabase(context: Context): TvShowRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                  return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TvShowRoomDatabase::class.java,
                    "TVShows_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}