package com.s3enterprises.jewellowholesale.customViews

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.sales.Sale

class CardMonthWise: LinearLayout {

    private val month by lazy { findViewById<TextView>(R.id.month_name) }
    private val gold by lazy { findViewById<TextView>(R.id.gold) }
    private val cash by lazy { findViewById<TextView>(R.id.cash) }
    private val total by lazy { findViewById<TextView>(R.id.total) }

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
        inflate(context, R.layout.card_month_wise,this)
    }

    fun setData(sale: Sale){
        this.month.text = sale.name
        this.gold.text = sale.gold
        this.cash.text = sale.cash
        this.total.text = sale.total
    }

    fun setPartyData(sale: Sale) {
        this.month.text = sale.gold
        this.total.text = sale.total
    }

}