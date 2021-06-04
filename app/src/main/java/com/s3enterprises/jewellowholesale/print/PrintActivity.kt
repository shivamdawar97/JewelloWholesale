package com.s3enterprises.jewellowholesale.print

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.databinding.ActivityPrintBinding

class PrintActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrintBinding
    private lateinit var preferences: SharedPreferences
    private val PRINTER_KEY = "printer_name"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_print)
        preferences = getSharedPreferences("jewello_wholesale",Context.MODE_PRIVATE)
        setUp()


    }

    private fun setUp() = with(binding) {
        printerName = preferences.getString(PRINTER_KEY,"")

        saveBtn.setOnClickListener {
            if(!printerName.isNullOrBlank())
            preferences.edit().putString(PRINTER_KEY,printerName).apply()
            else Toast.makeText(this@PrintActivity,"Please enter printer name",Toast.LENGTH_LONG).show()
        }

        testPrint.setOnClickListener {
            if(!printerName.isNullOrBlank())
                JewelloBluetoothSocket.printData("Test print....\n\n\n",this@PrintActivity)
            else Toast.makeText(this@PrintActivity,"Please enter printer name",Toast.LENGTH_LONG).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding.saveBtn.callOnClick()
    }

}