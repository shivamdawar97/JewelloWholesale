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
import com.s3enterprises.jewellowholesale.database.models.Item
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

        binding.swipeToRefresh.setOnRefreshListener {
            binding.isLoading = true
            binding.swipeToRefresh.isRefreshing = false
            ItemsRepository.loadItems(true)
        }
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() = with(binding){
        isLoading = true
        ItemsRepository.loadItems()
        itemRecycler.layoutManager = LinearLayoutManager(this@ItemsActivity)

        searchField.onTextChanged {
            (itemRecycler.adapter as ItemsAdapter).filter.filter(it)
        }

        ItemsRepository.items.observeForever {
            isLoading = false
            itemRecycler.adapter = ItemsAdapter(it!!)
        }
    }

    fun addItem(v: View){
        startActivity(Intent(this,AddItem::class.java))
    }

}