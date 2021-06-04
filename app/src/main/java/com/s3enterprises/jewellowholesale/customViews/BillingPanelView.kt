package com.s3enterprises.jewellowholesale.customViews

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
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

        setUpPanel()

    }

    private fun setUpPanel() = with(binding) {
        autoCompleteTextView.onTextChanged {
            if(model!!.billNo.value==0) model!!.findParty(it.toString())
            if(model!!.party.value!=null) Utils.hideKeyboard(autoCompleteTextView)
        }

        billChanger.setOnPreviousListener{
            model!!.getPreviousBill()
        }
        billChanger.setOnNextListener{
            model!!.getNextBill()
        }

        binding.addParty.setOnClickListener {
            context.startActivity(Intent(context,AddParty::class.java))
        }

    }

    fun setViewModel(viewModel:BillingViewModel){
        binding.model = viewModel
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