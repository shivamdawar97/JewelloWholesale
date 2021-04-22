package com.s3enterprises.jewellowholesale.billing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.settings.SettingsActivity

class BillingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing)
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