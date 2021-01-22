package com.example.tabatatimer.repository

import androidx.lifecycle.LiveData
import com.example.tabatatimer.database.TimerDatabaseDao
import com.example.tabatatimer.model.Timer

class TimerRepository(private val timerDatabaseDao: TimerDatabaseDao) {

    val readAllData: LiveData<List<Timer>> = timerDatabaseDao.readAllData()
    //val get: LiveData<Timer> = timerDatabaseDao.get()

    suspend fun addTimer(timer: Timer) {
        timerDatabaseDao.addTimer(timer)
    }

    suspend fun update(timer: Timer) {
        timerDatabaseDao.update(timer)
    }

    suspend fun delete(timer: Timer) {
        timerDatabaseDao.delete(timer)
    }

    suspend fun deleteAll() {
        timerDatabaseDao.deleteAll()
    }

    suspend fun get(id: Long) : Timer? {
        return timerDatabaseDao.get(id)
    }

    //val get: LiveData<Long<Timer>> = timerDatabaseDao.get()
}