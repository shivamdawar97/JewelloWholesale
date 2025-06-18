package com.s3enterprises.jewellowholesale.customViews

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils.onTextChanged
import com.s3enterprises.jewellowholesale.Utils.roundOff
import com.s3enterprises.jewellowholesale.database.models.BillItem
import com.s3enterprises.jewellowholesale.rx.RxBus
import com.s3enterprises.jewellowholesale.rx.RxEvent

class BillItemCardView(context: Context,private val item:BillItem):LinearLayout(context) {

    private var fineView: TextView
    private val itemWeightView by lazy { findViewById<FloatEditText>(R.id.item_weight) }

    init {
        inflate(context, if (item.isStone) R.layout.card_bill_stone_item else R.layout.card_bill_item, this)
        fineView = findViewById(R.id.item_fine)
        findViewById<TextView>(R.id.item_name).text = item.name
        if(item.fine!=0f) fineView.text = item.fine.toString()

        if (item.isStone) {
            val netWeight = item.weight - item.stone
            findViewById<TextView>(R.id.item_net_weight)?.text = netWeight.roundOff(3).toString()
        }
        itemWeightView.apply {
            if (item.weight!=0f) setText(item.weight.toString())

            else requestFocus()

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

        findViewById<TextView>(R.id.item_remove).setOnClickListener {
            RxBus.publish(RxEvent.EventBillItemRemoved(item.iId))
            (parent as ViewGroup).removeView(this)
        }

        findViewById<FloatEditText>(R.id.item_stone_weight)?.apply {
            setText(item.stone.toString())
            onTextChanged {
                item.stone = floatValue
                calculate()
            }
        }

    }

    private fun calculate() {
        val netWeight = item.weight - if (item.isStone) item.stone else 0f
        val c = netWeight * item.rate
        item.fine = (c/100).roundOff(3)
        fineView.text = item.fine.toString()
        if (item.isStone) {
            findViewById<TextView>(R.id.item_net_weight)?.text = netWeight.roundOff(3).toString()
        }
        RxBus.publish(RxEvent.EventBillItemChanged())
    }

}