package com.example.tabatatimer

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.navigation.navArgs
import com.example.tabatatimer.enums.Action
import com.example.tabatatimer.service.TimerService
import kotlinx.android.synthetic.main.activity_training.*
import java.util.ArrayList

class TrainingActivity : AppCompatActivity() {

    private val args by navArgs<TrainingActivityArgs>()
    private lateinit var receiver: BroadcastReceiver

    var valueStatusPause: String? = ""
    var valueTimePause: String? = ""
    lateinit var adapter: ArrayAdapter<*>
    var trainingSteps: ArrayList<String?> = ArrayList<String?>()
    var element = 0
    var checkLastSec = false
    var elementValuePause = 0

    companion object {
        val PARAM_START_TIME = "start_time"
        val NAME_ACTION = "name"
        val TIME_ACTION = "time"
        val CURRENT_ACTION = "pause"
        val BROADCAST_ACTION = "com.example.tabatatimer"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.getStringExtra(CURRENT_ACTION) == Action.Work.action) {
                    val task = intent.getStringExtra(NAME_ACTION)
                    val status = intent.getStringExtra(TIME_ACTION)!!
                    if (status == "1") {
                       workLastSec()
                    } else {
                        workInProgress(task)
                    }
                    trainingStep.text = task
                    trainingTime.text = status
                } else if (intent.getStringExtra(CURRENT_ACTION) == Action.Clear.action) {
                    clear()
                } else {
                    valueStatusPause = intent.getStringExtra(NAME_ACTION)
                    valueTimePause = intent.getStringExtra(TIME_ACTION)
                    assert(valueTimePause != null)
                    startPause(valueTimePause)
                }


            }
        }
        val intentFilter = IntentFilter(BROADCAST_ACTION)
        registerReceiver(receiver, intentFilter)

        btnStartStop.setOnClickListener { view: View? -> onStartClick(view) }
        btnStartStop.background = resources.getDrawable(R.drawable.start_sign)

        fillAdapter()
        adapter = ArrayAdapter<String?>(this, android.R.layout.simple_list_item_1, trainingSteps)
        listTraining.adapter = adapter
        listTraining.onItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View, position: Int, id: Long ->
                changeFieldListView(
                    view,
                    position
                )
            }

        listTraining.setOnScrollListener(object : AbsListView.OnScrollListener{
            override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {

            }

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                for ( i in 0 until visibleItemCount ) {
                    listTraining.getChildAt(i).setBackgroundColor(resources.getColor(R.color.colorPrimary))
                }
                if (element >= firstVisibleItem && element < firstVisibleItem + visibleItemCount && !checkLastSec) {
                    listTraining!!.getChildAt(element - firstVisibleItem).setBackgroundColor(resources.getColor(R.color.colorAccent))
                }
                if (element - 1 >= firstVisibleItem && element - 1 < firstVisibleItem + visibleItemCount && checkLastSec) {
                    listTraining!!.getChildAt(element - 1 - firstVisibleItem).setBackgroundColor(resources.getColor(R.color.colorAccent))
                }
            }
        })
    }

    fun startPause(status: String?) {
        if (status == "1") {
            if (!checkLastSec) element-- else checkLastSec = false
            val words = (adapter.getItem(element) as String).split(" : ".toRegex()).toTypedArray()
            valueStatusPause = words[1]
        }
        elementValuePause = element
    }

    fun stringForTimer(number: Int, name: String, time: Int): String {
        return "$number : $name : $time"
    }

    fun fillAdapter() {
        var number = 1
        var set: Int = args.MyTimer.sets
        if (args.MyTimer.preparation != 0) trainingSteps.add(stringForTimer(number++, resources.getString(R.string.Prepair), args.MyTimer.preparation))
        while (set > 0) {
            trainingSteps.add(stringForTimer(number++, resources.getString(R.string.Work), args.MyTimer.worktime))
            trainingSteps.add(stringForTimer(number++, resources.getString(R.string.Calm), args.MyTimer.restTime))
            set--
        }
        trainingSteps.add(number.toString() + " : " + getResources().getString(R.string.Finish))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun changeFieldListView(view: View, position: Int) {
        for (i in 0 until listTraining!!.lastVisiblePosition - listTraining!!.firstVisiblePosition + 1) {
            listTraining!!.getChildAt(i).setBackgroundColor(resources.getColor(R.color.colorPrimary))
        }
        element = position
        view.setBackgroundColor(resources.getColor(R.color.colorAccent))
        val intent = Intent(this, TimerService::class.java)
        this.stopService(intent)

        val words = (adapter.getItem(position) as String).split(" : ".toRegex()).toTypedArray()
        if (words.size == 2) {
            btnStartStop.background = resources.getDrawable(R.drawable.start_sign)
            btnStartStop.setOnClickListener { view: View? -> onStartClick(view) }
            AddNewService(words[1], "0")
        } else {
            btnStartStop.setOnClickListener { onResetClick(view) }
            btnStartStop.background = resources.getDrawable(R.drawable.stop)
            AddNewService(words[1], words[2])
        }
    }

    fun AddNewService(name: String?, time: String?) {
        val intent = Intent(this, TimerService::class.java)
                .putExtra(PARAM_START_TIME, time)
                .putExtra(NAME_ACTION, name)
        this.startService(intent)
    }

    fun onStartClick(view: View?) {
        if (valueStatusPause!!.isEmpty() || valueStatusPause == resources.getString(R.string.Finish)) {
            element = 0
            checkLastSec = false
            val intent = Intent(this, TimerService::class.java)
            this.stopService(intent)
            AddNewService(resources.getString(R.string.Prepair), args.MyTimer.preparation.toString())
            listTraining!!.getChildAt(listTraining!!.lastVisiblePosition - listTraining!!.firstVisiblePosition).setBackgroundColor(resources.getColor(R.color.colorPrimary))
            if (element >= listTraining!!.firstVisiblePosition && element <= listTraining!!.lastVisiblePosition) listTraining!!.getChildAt(element).setBackgroundColor(resources.getColor(R.color.colorAccent))
        } else {
            element = elementValuePause
            AddNewService(valueStatusPause, valueTimePause)
        }
        btnStartStop.setOnClickListener { view: View? -> onResetClick(view) }
        btnStartStop.background = resources.getDrawable(R.drawable.stop)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun onResetClick(view: View?) {
        btnStartStop.setOnClickListener { view: View? -> onStartClick(view) }
        btnStartStop.background = resources.getDrawable(R.drawable.start_sign)
        val intent = Intent(this, TimerService::class.java)
        this.stopService(intent)
    }

    fun workLastSec() {
        element++
        checkLastSec = true
        if (element < adapter!!.count) {
            val words = (adapter.getItem(element) as String).split(" : ".toRegex()).toTypedArray()

            if (words.size == 2) AddNewService(words[1], "0") else AddNewService(words[1], words[2])
        }

    }

    fun workInProgress(task: String?) {
        if (checkLastSec) {
            checkLastSec = false
        }
        valueStatusPause = ""
        if (task == resources.getString(R.string.Finish)) {
            btnStartStop.setOnClickListener { view: View? -> onStartClick(view) }
            btnStartStop.background = resources.getDrawable(R.drawable.start_sign)
        }
    }

    fun clear() {
        if (element != 0 && element - 1 - listTraining!!.firstVisiblePosition < 14 &&
            element - 1 - listTraining!!.firstVisiblePosition >= 0 && element != 0) {
                listTraining!!.getChildAt(element - listTraining!!.firstVisiblePosition - 1)
                    .setBackgroundColor(resources.getColor(R.color.colorPrimary))
        }
        if (element - listTraining!!.firstVisiblePosition < 14 && element - 1 - listTraining!!
                .firstVisiblePosition >= 0) listTraining!!.getChildAt(element - listTraining!!
            .firstVisiblePosition).setBackgroundColor(resources.getColor(R.color.colorAccent))
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, TimerService::class.java)
        this.stopService(intent)
    }

    override fun onBackPressed() {
        openDialog()

    }

    fun openDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        builder.setPositiveButton(R.string.Yes) {_, _ ->
            unregisterReceiver(receiver)
            val intent = Intent(this, TimerService::class.java)
            this.stopService(intent)
            finish()

        }
        builder.setNegativeButton("Нет") {_, _ ->

        }
        builder.setTitle(R.string.Exit)
        builder.show()
    }


}