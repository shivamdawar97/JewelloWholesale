package com.s3enterprises.jewellowholesale.billing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.database.models.Item
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.databinding.ActivityBillingBinding
import com.s3enterprises.jewellowholesale.items.ItemsRepository
import com.s3enterprises.jewellowholesale.party.PartyRepository
import com.s3enterprises.jewellowholesale.settings.SettingsActivity
import kotlinx.coroutines.launch

class BillingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBillingBinding
    private lateinit var items : List<Item>
    private lateinit var parties : List<Party>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = DataBindingUtil.setContentView(this,R.layout.activity_billing)
         initializeSetup()

    }

    private fun initializeSetup() = lifecycleScope.launch{
        items = ItemsRepository.getItems()
        parties = PartyRepository.getParties()

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