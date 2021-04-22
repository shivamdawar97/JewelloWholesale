package com.s3enterprises.jewellowholesale

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.s3enterprises.jewellowholesale.billing.BillingActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent(this,BillingActivity::class.java))
        finish()
    }
}