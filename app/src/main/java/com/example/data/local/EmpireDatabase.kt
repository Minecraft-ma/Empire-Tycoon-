package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LeaderboardScoreEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EmpireDatabase : RoomDatabase() {

    abstract fun leaderboardDao(): LeaderboardDao

    companion object {
        @Volatile
        private var INSTANCE: EmpireDatabase? = null

        fun getInstance(context: Context): EmpireDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EmpireDatabase::class.java,
                    "empire_tycoon_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
