package com.s3enterprises.jewellowholesale.sales

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.customViews.MonthYearPickerDialog
import com.s3enterprises.jewellowholesale.databinding.ActivitySalesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class SalesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalesBinding
    private val viewModel by viewModels<SalesViewModel>()
    private lateinit var monthPicker : MonthYearPickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sales)
        binding.viewModel = viewModel
        title = "Sales"
        setUp()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        monthPicker = MonthYearPickerDialog(viewModel.date.value!!).apply {
            setListener { _, year, month, dayOfMonth ->
                Calendar.getInstance().also {
                    it.time = date
                    it.set(Calendar.YEAR,year) ; it.set(Calendar.MONTH,month) ; it.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                    viewModel.date.value = it.time
                }
            }
        }

        viewModel.date.observeForever {
            Toast.makeText(this,Utils.getMonthDate(it),Toast.LENGTH_LONG).show()
        }
        binding.monthPicker.setOnClickListener {
            monthPicker.show(supportFragmentManager,"MonthYearPickerDialog")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setUp() = lifecycleScope.launch {

    }
}