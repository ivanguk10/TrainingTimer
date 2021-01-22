package com.example.tabatatimer.timers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.tabatatimer.R
import com.example.tabatatimer.fragments.TimersFragmentDirections
import com.example.tabatatimer.model.Timer
import kotlinx.android.synthetic.main.text_item_view.view.*

class TimerAdapter : RecyclerView.Adapter<TimerAdapter.TimerViewHolder>() {

    private var timerList = emptyList<Timer>()

    class TimerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        return TimerViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.text_item_view, parent, false))
    }

    override fun getItemCount(): Int {
        return timerList.size
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        val currentItem = timerList[position]
        //holder.itemView.id_txt.text = currentItem.timerId.toString()
        holder.itemView.name_txt.text = currentItem.name
        holder.itemView.itemLayout.setBackgroundColor(currentItem.color)
//        holder.itemView.startButton.setOnClickListener {
//            val startTimer = TimersFragmentDirections.actionTimersFragmentToTrainingTimerFragment(currentItem)
//            holder.itemView.findNavController().navigate(startTimer)
//        }

        holder.itemView.startButton.setOnClickListener {
            val startTimer = TimersFragmentDirections.actionTimersFragmentToTrainingActivity(currentItem)
            holder.itemView.findNavController().navigate(startTimer)
        }


        holder.itemView.itemLayout.setOnClickListener {
            val action = TimersFragmentDirections.actionTimersFragmentToUpdateTimerFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
    }

    fun setData(timer: List<Timer>) {
        this.timerList = timer
        notifyDataSetChanged()
    }
}