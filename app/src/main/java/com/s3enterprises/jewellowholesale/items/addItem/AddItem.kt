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
import com.s3enterprises.jewellowholesale.database.dao.ItemDao
import com.s3enterprises.jewellowholesale.database.models.Item
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.databinding.ActivityAddItemBinding
import com.s3enterprises.jewellowholesale.items.ItemsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddItem : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding
    private lateinit var item:Item
    @Inject lateinit var itemsRepository: ItemsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_item)
        title = "Add Item"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initializeSetup()

    }

    private fun initializeSetup() = with(binding){
        isLoading = false; isUpdate = false
        rate = 0f
        intent.extras?.let {
            val i = it.get("item") as? Item
            if(i!=null){
                item = i ;isUpdate = true
                itemName = i.name
                rate = i.rate
                rateField.setText(rate.toString())
            }
        }
        rateField.onTextChanged {
            binding.rate = stringToFloat(it.toString())
        }
        setUpClickListener()
    }

    private fun setUpClickListener() = with(binding){
        binding.addItem.setOnClickListener {
            if(itemName.isNullOrBlank() || rate!!.isNaN())
                Toast.makeText(this@AddItem,"Please fill all fields",Toast.LENGTH_LONG).show()
            else lifecycleScope.launch {
                isLoading = true
                if(!isUpdate!!) {
                    val pos = itemsRepository.getCount()
                    item = Item(name = itemName!!,rate = binding.rate!!, position = pos)
                    itemsRepository.insert(item)
                    Toast.makeText(this@AddItem,"Item Added ${item.name}",Toast.LENGTH_LONG).show()
                }
                else {
                    item.name = itemName!!
                    item.rate = rate!!
                    itemsRepository.update(item)
                    Toast.makeText(this@AddItem,"Item Updated ${item.name}",Toast.LENGTH_LONG).show()
                }
                isLoading = false

                finish()
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}