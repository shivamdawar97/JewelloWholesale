package com.s3enterprises.jewellowholesale.items.addItem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils.onTextChanged
import com.s3enterprises.jewellowholesale.Utils.stringToFloat
import com.s3enterprises.jewellowholesale.database.models.Item
import com.s3enterprises.jewellowholesale.databinding.ActivityAddItemBinding
import com.s3enterprises.jewellowholesale.items.ItemsRepository
import kotlinx.coroutines.launch

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
        setUpClickListener()
    }

    private fun setUpClickListener() = with(binding){
        binding.addItem.setOnClickListener {
            if(itemName.isNullOrBlank())
                Toast.makeText(this@AddItem,"Please fill all fields",Toast.LENGTH_LONG).show()
            else lifecycleScope.launch {
                isLoading = true
                val item = Item(name = itemName!!,rate = binding.rate!!)
                ItemsRepository.insert(item)
                isLoading = false
                Toast.makeText(this@AddItem,"Item Added ${item.name}",Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}