package com.s3enterprises.jewellowholesale.customViews

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.s3enterprises.jewellowholesale.databinding.DialogMonthYearPickerBinding
import java.util.*

class MonthYearPickerDialog(val date: Date = Date()) : DialogFragment()  {
    companion object {
        private const val MAX_YEAR = 2050
    }

    private lateinit var binding: DialogMonthYearPickerBinding

    private var listener: DatePickerDialog.OnDateSetListener? = null

    fun setListener(listener: DatePickerDialog.OnDateSetListener?) {
        this.listener = listener
    }

    private fun callOnClick() {
        listener?.onDateSet(null, binding.pickerYear.value, binding.pickerMonth.value, 1)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogMonthYearPickerBinding.inflate(requireActivity().layoutInflater)
        val cal: Calendar = Calendar.getInstance().apply { time = date }

        binding.pickerMonth.run {
            minValue = 0
            maxValue = 11
            value = cal.get(Calendar.MONTH)
            displayedValues = arrayOf("Jan","Feb","Mar","Apr","May","June","July",
                "Aug","Sep","Oct","Nov","Dec")
        }

        binding.pickerYear.run {
            val year = cal.get(Calendar.YEAR)
            minValue = year - 10
            maxValue = MAX_YEAR
            value = year
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Please Select View Month")
            .setView(binding.root)
            .setPositiveButton("Ok") { _, _ -> callOnClick() }
            .setNegativeButton("Cancel") { _, _ -> dialog?.cancel() }
            .create()
    }

}