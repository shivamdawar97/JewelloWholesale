package com.s3enterprises.jewellowholesale.sales

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.customViews.CardMonthWise
import com.s3enterprises.jewellowholesale.databinding.ActivitySalesBinding
import kotlinx.coroutines.launch

class SalesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sales)

        setUp()

    }

    private fun setUp() = lifecycleScope.launch {

        SalesRepository.getCurrentYearSale().forEach { sale ->
            when(sale.name){
                "party" -> {

                }
                "today" -> {
                    binding.gold = sale.gold
                    binding.cash = sale.cash
                    binding.total = sale.total
                }
                else -> {
                    val monthCard = CardMonthWise(this@SalesActivity)
                    monthCard.setData(sale)
                    binding.monthWiseContainer.addView(monthCard)
                }
            }
        }


    }
}