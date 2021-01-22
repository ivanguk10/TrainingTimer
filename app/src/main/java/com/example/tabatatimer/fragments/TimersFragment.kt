package com.example.tabatatimer.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabatatimer.R
import com.example.tabatatimer.SettingsActivity
import com.example.tabatatimer.viewmodels.TimerViewModel
import com.example.tabatatimer.databinding.TimersFragmentBinding
import com.example.tabatatimer.timers.TimerAdapter
import kotlinx.coroutines.InternalCoroutinesApi

class TimersFragment : Fragment() {
    private lateinit var binding: TimersFragmentBinding
    @InternalCoroutinesApi
    private lateinit var timerViewModel: TimerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.timers_fragment,
            container,
            false
        )

        binding.lifecycleOwner = this


        return binding.root
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val adapter = TimerAdapter()
        val recyclerView = binding.ListTimer
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)
        timerViewModel.readAllData.observe(viewLifecycleOwner, Observer { timer ->
            adapter.setData(timer)
        })

        binding.buttonAddTimer.setOnClickListener { findNavController()
            .navigate(R.id.action_timersFragment_to_createTimerFragment) }

        binding.SettingsButton.setOnClickListener { findNavController()
            .navigate(R.id.action_timersFragment_to_settingsActivity) }


    }



}