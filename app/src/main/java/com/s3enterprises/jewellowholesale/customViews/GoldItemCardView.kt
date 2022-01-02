package com.s3enterprises.jewellowholesale.customViews

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils.onTextChanged
import com.s3enterprises.jewellowholesale.Utils.roundOff
import com.s3enterprises.jewellowholesale.database.models.GoldItem
import com.s3enterprises.jewellowholesale.rx.RxBus
import com.s3enterprises.jewellowholesale.rx.RxEvent

class GoldItemCardView(context: Context, private val item: GoldItem): LinearLayout(context) {

    init {
        inflate(context, R.layout.card_gold_recv,this)
        if(item.fine!=0f) findViewById<TextView>(R.id.gold_rcv_fine).text = item.fine.toString()

        findViewById<FloatEditText>(R.id.gold_rcv).apply {
            if(item.weight!=0f) setText(item.weight.toString())
            else requestFocus()
            onTextChanged {
                item.weight = floatValue
                calculate()
            }
        }

        findViewById<FloatEditText>(R.id.gold_rcv_rate).apply {
            setText(item.purity.toString())
            onTextChanged {
                item.purity = floatValue
                calculate()
            }
        }

        findViewById<TextView>(R.id.item_remove).setOnClickListener {
            RxBus.publish(RxEvent.EventGoldItemRemoved(item))
            (parent as ViewGroup).removeView(this)
        }

    }

    private fun calculate() {
        val c = item.weight * item.purity
        item.fine = (c/100).roundOff(3)
        findViewById<TextView>(R.id.gold_rcv_fine).text = item.fine.toString()
        RxBus.publish(RxEvent.EventBillItemChanged())
    }


}