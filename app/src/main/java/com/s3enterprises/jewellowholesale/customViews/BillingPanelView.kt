package com.s3enterprises.jewellowholesale.customViews

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.Utils.onTextChanged
import com.s3enterprises.jewellowholesale.billing.AutoCompleteAdapter
import com.s3enterprises.jewellowholesale.billing.BillingActivity
import com.s3enterprises.jewellowholesale.billing.BillingViewModel
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.databinding.ViewBillingPanelBinding
import com.s3enterprises.jewellowholesale.party.addParty.AddParty
import com.s3enterprises.jewellowholesale.rx.RxBus
import com.s3enterprises.jewellowholesale.rx.RxEvent

class BillingPanelView: LinearLayout {

    private lateinit var binding : ViewBillingPanelBinding
    private val autoCompleteTextView by lazy { (binding.nameField.editText as AutoCompleteTextView) }

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

    }


    fun setViewModel(viewModel:BillingViewModel) = with(binding){
        model = viewModel

        autoCompleteTextView.onTextChanged {
            if(viewModel.billNo.value==0) model!!.findParty(it.toString())
            if(viewModel.party.value!=null) Utils.hideKeyboard(autoCompleteTextView)
        }

        billChanger.setOnPreviousListener{
            viewModel.getPreviousBill()
        }
        billChanger.setOnNextListener{
            viewModel.getNextBill()
        }

        addParty.setOnClickListener {
            context.startActivity(Intent(context,AddParty::class.java))
        }

        goldRcvRate.setText(viewModel.goldPurity.toString())
        bhavEdit.onTextChanged {
            viewModel.goldBhav = bhavEdit.floatValue.toInt()
            viewModel.calculate()
            RxBus.publish(RxEvent.BhavUpdated())
        }

        goldRcv.onTextChanged {
            viewModel.goldWeight = goldRcv.floatValue
            viewModel.calculate()
        }

        goldRcvRate.onTextChanged {
            viewModel.goldPurity = goldRcvRate.floatValue
            viewModel.calculate()
        }

        cashRcv.onTextChanged {
            viewModel.cashReceived = cashRcv.floatValue.toInt()
            viewModel.calculate()
        }

    }

    fun setPartiesAdapter(parties: List<Party>) {
        val adapter = AutoCompleteAdapter(context, R.layout.list_item,parties)
        autoCompleteTextView.setAdapter(adapter)
    }

    fun setBillNo(billNo:Int) {
        autoCompleteTextView.isEnabled = billNo == 0
        binding.billChanger.setBillNo(billNo)
    }

    fun clear() {
        autoCompleteTextView.setText("")
    }

    fun setPartyName(name: String) {
        if(!binding.model!!.isBillNotFound.value!!){
            autoCompleteTextView.setText(name)
            autoCompleteTextView.dismissDropDown()
        }
    }

}