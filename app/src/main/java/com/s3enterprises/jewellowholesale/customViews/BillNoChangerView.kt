package com.s3enterprises.jewellowholesale.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.s3enterprises.jewellowholesale.R

class BillNoChangerView: LinearLayout {

    private val billNoLabel by lazy { findViewById<TextView>(R.id.bill_no) }
    private val btnPrevious by lazy { findViewById<AppCompatImageView>(R.id.btn_previous) }
    private val btnNext by lazy { findViewById<AppCompatImageView>(R.id.btn_next) }

    constructor(context: Context) : super(context) {
        inflateLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        inflateLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        inflateLayout(context)
    }

    private fun inflateLayout(context: Context) {
        inflate(context, R.layout.view_bill_no_changer,this)
    }

    fun setBillNo(no:Int) {
        billNoLabel.text = if(no==0) "New Bill" else no.toString()
    }

    fun setOnNextListener(onClick:OnClickListener){
        btnNext.setOnClickListener(onClick)
    }
    fun setOnPreviousListener(onClick:OnClickListener){
        btnPrevious.setOnClickListener(onClick)
    }


}