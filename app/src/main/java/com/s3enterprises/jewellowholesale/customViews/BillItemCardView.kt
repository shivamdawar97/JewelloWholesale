package com.s3enterprises.jewellowholesale.customViews

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils.onTextChanged
import com.s3enterprises.jewellowholesale.Utils.roundOff
import com.s3enterprises.jewellowholesale.database.models.BillItem
import com.s3enterprises.jewellowholesale.rx.RxBus
import com.s3enterprises.jewellowholesale.rx.RxEvent

class BillItemCardView(context: Context,private val item:BillItem):LinearLayout(context) {

    private var fineView: TextView

    init {
        inflate(context, R.layout.card_bill_item,this)
        fineView = findViewById(R.id.item_fine)
        findViewById<TextView>(R.id.item_name).text = item.name

        findViewById<FloatEditText>(R.id.item_weight).apply {
            if(item.weight!=0f) setText(item.weight.toString())
            onTextChanged {
                item.weight = floatValue
                calculate()
            }
        }

        findViewById<FloatEditText>(R.id.item_rate).apply {
            setText(item.rate.toString())
            onTextChanged {
                item.rate = floatValue
                calculate()
            }
        }
    }

    private fun calculate() {
        val c = item.weight * item.rate
        item.fine = c/100
        fineView.text = item.fine.roundOff(3).toString()
        RxBus.publish(RxEvent.EventBillItemChanged())
    }

}