package com.example.tabatatimer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tabatatimer.database.TimerDatabase
import com.example.tabatatimer.repository.TimerRepository
import com.example.tabatatimer.model.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class TimerViewModel(application: Application) : AndroidViewModel(application) {
    val readAllData: LiveData<List<Timer>>
    private val repository: TimerRepository

    private var _timerId = MutableLiveData<Timer>()
    val timerId: MutableLiveData<Timer>
        get() = _timerId


    init {
        val timerDatabaseDao = TimerDatabase.getDatabase(application).timerDatabaseDao()
        repository = TimerRepository(timerDatabaseDao)
        readAllData = repository.readAllData
    }

    fun addTimer(timer: Timer) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTimer(timer)
        }
    }

    fun update(timer: Timer) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(timer)
        }
    }

    fun delete(timer: Timer) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(timer)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    fun get(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            timerId.postValue(repository.get(id))

        }
    }


    private var _name = MutableLiveData<String>("name")
    val name: LiveData<String>
        get() = _name


    private var _preparation = MutableLiveData(10)
    val preparation: LiveData<Int>
        get() = _preparation
//

    private var _workTime = MutableLiveData(20)
    val workTime: MutableLiveData<Int>
        get() = _workTime


    private var _restTime = MutableLiveData(10)
    val restTime: MutableLiveData<Int>
        get() = _restTime


    private var _restSets = MutableLiveData(10)
    val restSets: MutableLiveData<Int>
        get() = _restSets


    private var _sets = MutableLiveData(1)
    val sets: MutableLiveData<Int>
        get() = _sets

    private var _color = MutableLiveData(-16777216)
    val color: MutableLiveData<Int>
        get() = _color

    fun setColor() {
        color.value
    }



}