package com.example.tabatatimer.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import codes.side.andcolorpicker.converter.toColorInt
import codes.side.andcolorpicker.model.IntegerHSLColor
import com.example.tabatatimer.R
import com.example.tabatatimer.model.Timer
import com.example.tabatatimer.viewmodels.TimerViewModel
import com.example.tabatatimer.databinding.UpdateTimerFragmentBinding

import kotlinx.coroutines.InternalCoroutinesApi

class UpdateTimerFragment : Fragment() {

    private lateinit var binding: UpdateTimerFragmentBinding
    @InternalCoroutinesApi
    private lateinit var timerViewModel: TimerViewModel

    private val args by navArgs<UpdateTimerFragmentArgs>()
    //private val args: UpdateTimerFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.update_timer_fragment,
                container,
                false
        )

        binding.lifecycleOwner = this

        return binding.root
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)

        binding.updateInputName.setText(args.timer.name)
        binding.updateInputPrep.setText(args.timer.preparation.toString())
        binding.updateInputWork.setText(args.timer.worktime.toString())
        binding.updateInputRest.setText(args.timer.restTime.toString())
        binding.updateInputSet.setText(args.timer.sets.toString())
        binding.colorSeekBar.pickedColor = convertToIntegerHSLColor(args.timer.color)


        binding.update.setOnClickListener { updateItem() }
        binding.delete.setOnClickListener { deleteTimer() }

    }

    @InternalCoroutinesApi
    private fun updateItem() {
        val name = binding.updateInputName.text.toString()
        val preparation = binding.updateInputPrep.text.toString().toInt()
        val worktime = binding.updateInputWork.text.toString().toInt()
        val restTime = binding.updateInputRest.text.toString().toInt()
        val sets = binding.updateInputSet.text.toString().toInt()
        val paint: IntegerHSLColor = binding.colorSeekBar.pickedColor
        val color = Color.HSVToColor(floatArrayOf(paint.floatH, paint.floatL, paint.floatS))

        val updatedTimer = Timer(args.timer.timerId, name, preparation, worktime, restTime,
            sets, color)

        timerViewModel.update(updatedTimer)
        Toast.makeText(requireContext(),"Updated successfully", Toast.LENGTH_SHORT).show()

        findNavController().navigate(R.id.action_updateTimerFragment_to_timersFragment)
    }

    @InternalCoroutinesApi
    private fun deleteTimer() {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        builder.setPositiveButton("Да") {_, _ ->
            timerViewModel.delete(args.timer)
            Toast.makeText(
                requireContext(),
                "${args.timer.name} успешно удален",
                Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateTimerFragment_to_timersFragment)
        }
        builder.setNegativeButton("Нет") {_, _ ->

        }
        builder.setTitle("Удалить ${args.timer.name}?")
        builder.setMessage("Вы уверены, что хотите удалить ${args.timer.name}?")
        builder.create().show()

    }

    fun convertToIntegerHSLColor(color: Int): IntegerHSLColor {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val integerHSLColor = IntegerHSLColor()
        integerHSLColor.floatH = hsv[0]
        integerHSLColor.floatL = hsv[1]
        integerHSLColor.floatS = hsv[2]
        return integerHSLColor
    }

}