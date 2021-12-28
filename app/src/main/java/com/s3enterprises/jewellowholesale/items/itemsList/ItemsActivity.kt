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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ItemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemsBinding
    @Inject lateinit var itemsRepository: ItemsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_items)
        title = "Items"
        setUpRecyclerView()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpRecyclerView() = with(binding){

        itemRecycler.layoutManager = LinearLayoutManager(this@ItemsActivity)

        searchField.onTextChanged {
            (itemRecycler.adapter as ItemsAdapter).filter.filter(it)
        }
        itemsRepository.items.observeForever {
            itemRecycler.adapter = ItemsAdapter(it!!)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun addItem(v: View){
        startActivity(Intent(this,AddItem::class.java))
    }

}