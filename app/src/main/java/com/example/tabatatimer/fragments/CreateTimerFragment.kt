package com.example.tabatatimer.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import codes.side.andcolorpicker.model.IntegerHSLColor
import com.example.tabatatimer.R
import com.example.tabatatimer.model.Timer
import com.example.tabatatimer.viewmodels.TimerViewModel
import com.example.tabatatimer.databinding.CreateTimerFragmentBinding
import kotlinx.coroutines.InternalCoroutinesApi

class CreateTimerFragment : Fragment() {

    private lateinit var binding: CreateTimerFragmentBinding
    @InternalCoroutinesApi
    private lateinit var timerViewModel: TimerViewModel
    private lateinit var timer: Timer

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.create_timer_fragment,
            container,
            false
        )

        timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)

        binding.lifecycleOwner = this


        timerViewModel.name.observe(viewLifecycleOwner, Observer { value ->
           binding.inputName.setText(value)
        })

        timerViewModel.preparation.observe(viewLifecycleOwner, Observer { value ->
            binding.inputPrep.setText(value.toString())
        })

        timerViewModel.workTime.observe(viewLifecycleOwner, Observer { value ->
            binding.inputWork.setText(value.toString())
        })

        timerViewModel.restTime.observe(viewLifecycleOwner, Observer { value ->
            binding.inputRest.setText(value.toString())
        })


        timerViewModel.sets.observe(viewLifecycleOwner, Observer { value ->
            binding.inputSet.setText(value.toString())
        })

        binding.submit.setOnClickListener { insertDataToDatabase() }

        return binding.root
    }

    @InternalCoroutinesApi
    fun insertDataToDatabase() {
        val name = binding.inputName.text.toString()
        val preparation = binding.inputPrep.text.toString().toInt()
        val worktime = binding.inputWork.text.toString().toInt()
        val restTime = binding.inputRest.text.toString().toInt()
        val sets = binding.inputSet.text.toString().toInt()
        val paint: IntegerHSLColor = binding.colorSeekBar.pickedColor
        val color = Color.HSVToColor(floatArrayOf(paint.floatH, paint.floatL, paint.floatS))

        val timer = Timer(0, name, preparation, worktime,
            restTime, sets, color)

        timerViewModel.addTimer(timer)
        findNavController().navigate(R.id.action_createTimerFragment_to_timersFragment)
        Toast.makeText(requireContext(), "Timer is successfully added", Toast.LENGTH_LONG).show()
    }
}