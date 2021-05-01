package com.s3enterprises.jewellowholesale.items.itemsList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.databinding.ActivityItemsBinding
import com.s3enterprises.jewellowholesale.items.ItemsRepository
import com.s3enterprises.jewellowholesale.items.addItem.AddItem
import com.s3enterprises.jewellowholesale.party.PartyRepository
import kotlinx.coroutines.launch

class ItemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_items)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() = lifecycleScope.launch {
        binding.isLoading = true
        val parties = ItemsRepository.getItems()
        binding.isLoading = false

    }

    fun addItem(v: View){
        startActivity(Intent(this,AddItem::class.java))
    }

}