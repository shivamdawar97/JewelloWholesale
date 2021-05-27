package com.s3enterprises.jewellowholesale.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils.onTextChanged
import com.s3enterprises.jewellowholesale.billing.AutoCompleteAdapter
import com.s3enterprises.jewellowholesale.billing.BillingActivity
import com.s3enterprises.jewellowholesale.billing.BillingViewModel
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.databinding.ViewBillingPanelBinding

class BillingPanelView: LinearLayout {

    private lateinit var binding : ViewBillingPanelBinding
    private val autoCompleteTextView by lazy { (binding.nameField.editText as AutoCompleteTextView) }
    val billChanger: BillNoChangerView
    get() = binding.billChanger

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
        binding.lifecycleOwner = context as BillingActivity
        autoCompleteTextView.onTextChanged {
            binding.model!!.findParty(it.toString())
        }
    }

    fun setViewModel(viewModel:BillingViewModel){
        binding.model = viewModel
    }

    fun setPartiesAdapter(parties: List<Party>) {
        val adapter = AutoCompleteAdapter(context, R.layout.list_item,parties)
        autoCompleteTextView.setAdapter(adapter)
    }

    fun setPartyError() {

    }

    fun clear() {
        autoCompleteTextView.setText("")
    }

}