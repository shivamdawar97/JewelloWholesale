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
import com.s3enterprises.jewellowholesale.database.models.Bill
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.databinding.ViewBillingPanelBinding
import com.s3enterprises.jewellowholesale.party.addParty.AddParty
import com.s3enterprises.jewellowholesale.rx.RxBus
import com.s3enterprises.jewellowholesale.rx.RxEvent

class BillingPanelView: LinearLayout {

    lateinit var binding : ViewBillingPanelBinding
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
        bhavEdit.setText(viewModel.goldBhav.toString())
        autoCompleteTextView.onTextChanged { if(!viewModel.listenChangeEvents) return@onTextChanged
            if(viewModel.loadedBill.value==null) model!!.findParty(it.toString())
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

        bhavEdit.onTextChanged { if(!viewModel.listenChangeEvents) return@onTextChanged
            viewModel.goldBhav = bhavEdit.floatValue.toInt()
            viewModel.calculate()
            if(viewModel.loadedBill.value == null) RxBus.publish(RxEvent.PreferencesUpdated())
        }

        cashRcv.onTextChanged { if(!viewModel.listenChangeEvents) return@onTextChanged
            viewModel.cashReceived = cashRcv.floatValue.toInt()
            viewModel.calculate()
        }

    }

    fun setPartiesAdapter(parties: List<Party>) {
        val adapter = AutoCompleteAdapter(context, R.layout.list_item,parties)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setText(" ")
        autoCompleteTextView.setText("")
    }

    fun clear() {
        autoCompleteTextView.setText("")
        autoCompleteTextView.isEnabled = true
        binding.cashRcv.setText("")
        binding.billChanger.setBillNo(0)
    }

    fun setUpBill(bill: Bill) = with(binding) {

        billChanger.setBillNo(bill.billNo)

        if(model!!.isBillNotFound.value == true) return@with

        autoCompleteTextView.isEnabled = false
        autoCompleteTextView.setText(bill.partyName)
        autoCompleteTextView.dismissDropDown()

        itemsContainer.removeAllViews()
        goldsContainer.removeAllViews()

        model?.billItemList?.forEach { billItem ->
            val view = BillItemCardView(context,billItem)
            itemsContainer.addView(view)
        }

        model?.goldItemList?.forEach { goldItem ->
            val view = GoldItemCardView(context,goldItem)
            goldsContainer.addView(view)
        }
        bhavEdit.setText(bill.bhav.toString())
        cashRcv.setText(bill.cashReceived.toString())
    }


}