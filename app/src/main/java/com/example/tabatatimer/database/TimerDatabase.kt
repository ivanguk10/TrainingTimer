package com.example.tabatatimer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tabatatimer.model.Timer
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [Timer::class], version = 1, exportSchema = false)
abstract class TimerDatabase : RoomDatabase() {

    abstract fun timerDatabaseDao(): TimerDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: TimerDatabase? = null

        @InternalCoroutinesApi
        fun getDatabase(context: Context): TimerDatabase {

            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TimerDatabase::class.java,
                    "timers_database"
                ).build()
                INSTANCE = instance
                return instance
            }

        }
    }
}

