package com.example.tabatatimer.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tabatatimer.model.Timer

@Dao
interface TimerDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTimer(timer: Timer)

    @Update
    suspend fun update(timer: Timer)

    @Query("SELECT * from timer_table WHERE timerId = :id")
    suspend fun get(id: Long): Timer?
    //fun get(id: Long): LiveData<Timer>

    @Query("SELECT * from timer_table WHERE timer_name = :timerName")
    suspend fun getByName(timerName: String): Timer

    @Query("SELECT * FROM timer_table ORDER BY timerId DESC")
    fun readAllData(): LiveData<List<Timer>>

    @Delete
    suspend fun delete(timer: Timer)

    @Query("DELETE FROM timer_table")
    suspend fun deleteAll();

}