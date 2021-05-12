package com.s3enterprises.jewellowholesale.customViews

import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils.onTextChanged
import com.s3enterprises.jewellowholesale.database.models.BillItem

class BillItemCardView(context: Context,private val item:BillItem):LinearLayout(context) {

    private var fineView: TextView

    init {
        inflate(context, R.layout.card_bill_item,this)
        fineView = findViewById(R.id.item_fine)
        findViewById<TextView>(R.id.item_name).text = item.name
        findViewById<EditText>(R.id.item_rate).apply {
            setText(item.rate.toString())
            onTextChanged {
                item.weight = it.toString().toFloat()
                calculate()
            }
        }
        findViewById<EditText>(R.id.item_weight).onTextChanged {
            item.rate = it.toString().toFloat()
            calculate()
        }

    }

    private fun calculate() {
        item.fine = item.weight * item.rate/100
    }

}