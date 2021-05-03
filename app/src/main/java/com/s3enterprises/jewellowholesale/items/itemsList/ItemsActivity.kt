package com.s3enterprises.jewellowholesale.items.itemsList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils.onTextChanged
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
        binding.swipeToRefresh.setOnRefreshListener {
            binding.swipeToRefresh.isRefreshing = false
            setUpRecyclerView(hardReload = true)
        }
    }

    private fun setUpRecyclerView(hardReload:Boolean=false) = lifecycleScope.launch {
        binding.isLoading = true
        val items = ItemsRepository.getItems(hardReload)
        binding.isLoading = false
        binding.itemRecycler.adapter = ItemsAdapter(items)
        binding.itemRecycler.layoutManager = LinearLayoutManager(this@ItemsActivity)
        binding.searchField.onTextChanged {
            (binding.itemRecycler.adapter as ItemsAdapter).filter.filter(it)
        }
    }

    fun addItem(v: View){
        startActivity(Intent(this,AddItem::class.java))
    }

}