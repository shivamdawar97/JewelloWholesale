package com.s3enterprises.jewellowholesale.bills

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.databinding.ActivityBillsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class BillsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBillsBinding
    private val viewModel by viewModels<BillsViewModel>()
    private val datePicker by lazy {
        MaterialDatePicker.Builder.datePicker().setTitleText("Select a date").build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_bills)
        binding.lifecycleOwner = this
        title = "Bills"
        binding.billsRecycler.layoutManager = LinearLayoutManager(this)
        binding.viewModel = viewModel
        binding.isListEmpty = false
        viewModel.bills.observeForever {
            binding.billsRecycler.adapter = BillsAdapter(it)
            binding.isListEmpty = it.isNullOrEmpty()
        }
        setUpDatePicker()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpDatePicker() {
        datePicker.addOnPositiveButtonClickListener {
            viewModel.date.value = Date(it)
        }
        binding.datePicker.setOnClickListener {
            datePicker.show(supportFragmentManager,"DATE_PICKER")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home)
        {   finish()
            return true
        }
        return false
    }

}