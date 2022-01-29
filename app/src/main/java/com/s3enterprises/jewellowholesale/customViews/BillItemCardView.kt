package com.s3enterprises.jewellowholesale.customViews

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.Utils.onTextChanged
import com.s3enterprises.jewellowholesale.Utils.roundOff
import com.s3enterprises.jewellowholesale.database.models.BillItem
import com.s3enterprises.jewellowholesale.rx.RxBus
import com.s3enterprises.jewellowholesale.rx.RxEvent


class BillItemCardView(context: Context,private val item:BillItem):LinearLayout(context) {

    private var fineView: TextView
    private val itemWeightView by lazy { findViewById<FloatEditText>(R.id.item_weight) }

    init {
        inflate(context, R.layout.card_bill_item,this)
        fineView = findViewById(R.id.item_fine)
        findViewById<TextView>(R.id.item_name).text = item.name
        if(item.fine!=0f) fineView.text = item.fine.toString()

        itemWeightView.apply {
            if(item.weight!=0f) setText(item.weight.toString())
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

        findViewById<TextView>(R.id.item_add_wt).setOnClickListener {
            showAddWeightDialog()
        }

    }

    private fun calculate() {
        val c = item.weight * item.rate
        item.fine = (c/100).roundOff(3)
        fineView.text = item.fine.toString()
        RxBus.publish(RxEvent.EventBillItemChanged())
    }

    private fun showAddWeightDialog() {
        val dialog = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_weight,null)
        dialog.setView(view)
        view.findViewById<TextView>(R.id.item_name).text = item.name
        view.findViewById<TextView>(R.id.item_weight).text = item.weight.toString()

        val finalWeightView = view.findViewById<TextView>(R.id.item_final_weight)
        view.findViewById<FloatEditText>(R.id.add_weight_et).apply {
            onTextChanged {
                val newValue = item.weight + floatValue
                finalWeightView.text = newValue.roundOff(3).toString()
            }
        }
        dialog.setPositiveButton("Ok") { d1,_ ->
            itemWeightView.setText(finalWeightView.text.toString())
            d1.dismiss()
        }
        dialog.setCancelable(false).setNegativeButton("Cancel") { d1, _ ->
            d1.dismiss()
        }
        dialog.setOnDismissListener {
            Utils.hideKeyboard(view)
        }
        dialog.create().show()
    }

}