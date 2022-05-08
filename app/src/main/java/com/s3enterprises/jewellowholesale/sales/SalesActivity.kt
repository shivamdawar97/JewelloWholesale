package com.s3enterprises.jewellowholesale.sales

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.Utils.roundOff
import com.s3enterprises.jewellowholesale.customViews.MonthYearPickerDialog
import com.s3enterprises.jewellowholesale.databinding.ActivitySalesBinding
import dagger.hilt.android.AndroidEntryPoint
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
        binding.lifecycleOwner = this; title = "Sales"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        monthPicker = MonthYearPickerDialog(viewModel.date.value!!).apply {
            setListener { _, year, month, dayOfMonth ->
                Calendar.getInstance().also {
                    it.time = date
                    it.set(Calendar.YEAR,year) ; it.set(Calendar.MONTH,month) ; it.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                    viewModel.year = year ; viewModel.month = month
                    viewModel.date.value = it.time
                }
            }
        }

        binding.monthPicker.setOnClickListener {
            monthPicker.show(supportFragmentManager,"MonthYearPicker")
        }

        binding.salesRecycler.layoutManager = LinearLayoutManager(this)
        viewModel.sales.observe(this) {
            var x1 = 0; var x2 = 0f; var x3 = 0f
            it.forEach { sale ->
                x1 += sale.cash ; x2 += sale.gold ; x3 += sale.stock
            }
            binding.salesRecycler.adapter = SalesRecyclerAdapter(it) { date ->
                showAlertDialog(date)
            }
            binding.cash.text = Utils.getRupeesFormatted(x1)
            binding.gold.text = x2.roundOff(3).toString()
            binding.stock.text = x3.roundOff(3).toString()
        }
    }

    private fun showAlertDialog(date:Long) {
        AlertDialog.Builder(this)
            .setTitle("Delete this sale")
            .setNegativeButton("Cancel"){ d,_->
                d.dismiss()
            }
            .setPositiveButton("Yes") { d,_ ->
                viewModel.deleteSale(date)
                d.dismiss()
            }.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}