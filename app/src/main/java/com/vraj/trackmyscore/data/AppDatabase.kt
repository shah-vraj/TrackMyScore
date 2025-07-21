package com.vraj.trackmyscore.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vraj.trackmyscore.data.dao.PlayerDao
import com.vraj.trackmyscore.data.entity.PlayerEntity

/**
 * The [Room] database for this app.
 */
@Database(
    entities = [PlayerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getPlayerDao(): PlayerDao

    companion object {
        private const val DATABASE_NAME = "track-my-score-database"

        @Volatile private var instance: AppDatabase? = null

        /**
         * Build and return [RoomDatabase] instance of the app.
         *
         * @param [context] application context
         *
         * @return [AppDatabase] instance
         */
        fun buildDatabase(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: let {
                instance = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .build()

                instance!!
            }
        }
    }
}