package com.example.tabatatimer.service

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import android.os.IBinder
import com.example.tabatatimer.R
import com.example.tabatatimer.TrainingActivity
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class TimerService : Service() {

    var service: ScheduledExecutorService? = null
    var soundPool: SoundPool? = null
    var soundPrepair = 0
    var soundFinal = 0
    var currentTime = 0
    var name: String? = null
    var scheduledFuture: ScheduledFuture<*>? = null


    @InternalCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        soundPool = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
        soundPrepair = soundPool!!.load(this, R.raw.tick, 1)
        soundFinal = soundPool!!.load(this, R.raw.tick, 1)
        service = Executors.newScheduledThreadPool(1)
    }

    override fun onDestroy() {
        service?.shutdownNow()
        scheduledFuture?.cancel(true)
        val intent = Intent(TrainingActivity.BROADCAST_ACTION)
        intent.putExtra(TrainingActivity.CURRENT_ACTION, "pause")
        intent.putExtra(TrainingActivity.NAME_ACTION, name)
        intent.putExtra(TrainingActivity.TIME_ACTION, currentTime.toString().toInt())
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val time = intent?.getStringExtra(TrainingActivity.PARAM_START_TIME)?.toInt()
        name = intent?.getStringExtra(TrainingActivity.NAME_ACTION)
        val mr = time?.let { MyTimerTask(startId, it, name) }
        if (scheduledFuture != null) {
            service!!.schedule({
                scheduledFuture!!.cancel(true)
                if (time != null) {
                    scheduledFuture =
                        service!!.scheduleAtFixedRate(mr, 0, (time + 1).toLong(), TimeUnit.SECONDS)
                }
            }, 1000, TimeUnit.MILLISECONDS)
        } else {
            if (time != null) {
                scheduledFuture =
                    service!!.scheduleAtFixedRate(mr, 0, (time + 1).toLong(), TimeUnit.SECONDS)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }



    internal inner class MyTimerTask(var startId: Int, var time: Int, var name: String?) : TimerTask() {
        override fun run() {
            var intent = Intent(TrainingActivity.BROADCAST_ACTION)
            if (name == resources.getString(R.string.Finish)) {
                intent.putExtra(TrainingActivity.CURRENT_ACTION, "work")
                intent.putExtra(TrainingActivity.NAME_ACTION, name)
                intent.putExtra(TrainingActivity.TIME_ACTION, "")
                sendBroadcast(intent)
            }
            try {
                currentTime = time
                while (currentTime > 0) {
                    intent.putExtra(TrainingActivity.CURRENT_ACTION, "work")
                    intent.putExtra(TrainingActivity.NAME_ACTION, name)
                    intent.putExtra(
                        TrainingActivity.TIME_ACTION,
                            Integer.toString(currentTime)
                    )
                    sendBroadcast(intent)
                    TimeUnit.SECONDS.sleep(1)
                    signalSound(currentTime)
                    currentTime--
                }
                intent = Intent(TrainingActivity.BROADCAST_ACTION)
                intent.putExtra(TrainingActivity.CURRENT_ACTION, "clear")
                sendBroadcast(intent)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        fun signalSound(time: Int) {
            if (time <= 4) {
                if (time == 1) soundPool!!.play(soundFinal, 1f, 1f, 0, 0, 1f)
                else soundPool!!.play(soundPrepair, 1f, 1f, 0, 0, 1f)
            }
        }

    }
}