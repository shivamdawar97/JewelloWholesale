package com.s3enterprises.jewellowholesale.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.bills.BillsActivity
import com.s3enterprises.jewellowholesale.databinding.ActivitySettingsBinding
import com.s3enterprises.jewellowholesale.items.itemsList.ItemsActivity
import com.s3enterprises.jewellowholesale.party.partyList.PartiesActivity
import com.s3enterprises.jewellowholesale.sales.SalesActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        setUpListView()

    }

    private fun setUpListView() {
        val list = arrayOf("Parties","Bills","items","sales")
        binding.listview.adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, list)
        binding.listview.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            when(position){
                0 ->startActivity(Intent(this,PartiesActivity::class.java))
                1-> startActivity(Intent(this,BillsActivity::class.java))
                2->startActivity(Intent(this,ItemsActivity::class.java))
                3->startActivity(Intent(this,SalesActivity::class.java))

            }
        }
    }
}