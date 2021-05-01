package com.s3enterprises.jewellowholesale.items.addItem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils.onTextChanged
import com.s3enterprises.jewellowholesale.Utils.stringToFloat
import com.s3enterprises.jewellowholesale.databinding.ActivityAddItemBinding

class AddItem : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_item)
        initializeSetup()
    }

    private fun initializeSetup() = with(binding){
        isLoading = false; isUpdate = false
        rate = 0f; rateField.onTextChanged {
            binding.rate = stringToFloat(it.toString())
        }
    }

    fun addItem(v: View) = with(binding) {

    }
}