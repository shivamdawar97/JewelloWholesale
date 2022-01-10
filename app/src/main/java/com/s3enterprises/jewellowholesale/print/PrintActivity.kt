package com.s3enterprises.jewellowholesale.print

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.databinding.ActivityPrintBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PrintActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrintBinding
    @Inject lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_print)
        title = "Printer Setting"
        setUp()
    }

    private fun setUp() = with(binding) {
        printerName = Utils.printerName

        saveBtn.setOnClickListener {
            if(!printerName.isNullOrBlank())
            preferences.edit().putString("printer_name",printerName).apply().also { Utils.printerName = printerName!! }
            else Toast.makeText(this@PrintActivity,"Please enter printer name",Toast.LENGTH_LONG).show()
        }

        testPrint.setOnClickListener {
            if(!printerName.isNullOrBlank())
                lifecycleScope.launch {
                    JewelloBluetoothSocket().printData("Test print....\n\n\n",this@PrintActivity)
                }
            else Toast.makeText(this@PrintActivity,"Please enter printer name",Toast.LENGTH_LONG).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding.saveBtn.callOnClick()
    }

}