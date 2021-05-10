package com.s3enterprises.jewellowholesale.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.s3enterprises.jewellowholesale.billing.BillingViewModel
import com.s3enterprises.jewellowholesale.databinding.ViewBillingPanelBinding

class BillingPanelView: LinearLayout {

    private lateinit var binding : ViewBillingPanelBinding

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
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ViewBillingPanelBinding.inflate(inflater,this,true)

    }

    fun setViewModel(viewModel:BillingViewModel){
        binding.model = viewModel
    }

}