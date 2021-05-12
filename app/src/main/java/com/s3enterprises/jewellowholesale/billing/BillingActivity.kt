package com.s3enterprises.jewellowholesale.billing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.customViews.BillItemCardView
import com.s3enterprises.jewellowholesale.database.models.BillItem
import com.s3enterprises.jewellowholesale.database.models.Item
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.databinding.ActivityBillingBinding
import com.s3enterprises.jewellowholesale.items.ItemsRepository
import com.s3enterprises.jewellowholesale.party.PartyRepository
import com.s3enterprises.jewellowholesale.settings.SettingsActivity
import kotlinx.coroutines.launch

class BillingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBillingBinding
    private val viewModel by viewModels<BillingViewModel>()
    private lateinit var itemsContainer:LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = DataBindingUtil.setContentView(this,R.layout.activity_billing)
         itemsContainer = findViewById(R.id.items_container)
         initializeSetup()
         binding.billingPanel.setViewModel(viewModel)

    }

    private fun initializeSetup() = with(binding){
        isItemsListVisible = false

        viewModel.itemNamesList.observeForever { items ->
            itemsList.adapter =
                ArrayAdapter(this@BillingActivity,android.R.layout.simple_expandable_list_item_1,items)
        }

        itemsList.setOnItemClickListener { _, _, pos,_ ->
            val i = viewModel.items[pos]
            val billItem = BillItem(i.iId,i.name,rate = i.rate)
            val view = BillItemCardView(this@BillingActivity,billItem)
            itemsContainer.addView(view)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.billing_options_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.reset -> {}
            R.id.settings -> startActivity(Intent(this,SettingsActivity::class.java))
            R.id.send_pending -> {}
            R.id.view_pending -> {}
        }
        return true
    }
}