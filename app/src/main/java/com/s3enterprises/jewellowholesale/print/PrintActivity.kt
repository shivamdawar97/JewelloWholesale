package com.s3enterprises.jewellowholesale.print

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
                    val socket = JewelloBluetoothSocket()
                    val bold = with(bold.text.toString()) { if(isNullOrBlank()) 0 else toInt() }
                    val width = with(width.text.toString()) { if(isNullOrBlank()) 0 else toInt() }
                    val height = with(height.text.toString()) { if(isNullOrBlank()) 0 else toInt() }
                    socket.findDeviceAndConnect(this@PrintActivity)
                    socket.printCustomData(bold,width,height,"${text.text}\n${bold} ${width} ${height}\n")
                    socket.disconnectBT()
                }
            else Toast.makeText(this@PrintActivity,"Please enter printer name",Toast.LENGTH_LONG).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding.saveBtn.callOnClick()
    }

}