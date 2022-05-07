package com.s3enterprises.jewellowholesale.customViews

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.size
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.Utils.INPUT_CONNECTION
import com.s3enterprises.jewellowholesale.Utils.onTextChanged
import com.s3enterprises.jewellowholesale.Utils.roundOff
import com.s3enterprises.jewellowholesale.billing.AutoCompleteAdapter
import com.s3enterprises.jewellowholesale.billing.BillingActivity
import com.s3enterprises.jewellowholesale.billing.BillingViewModel
import com.s3enterprises.jewellowholesale.database.models.Bill
import com.s3enterprises.jewellowholesale.database.models.GoldItem
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.databinding.ViewBillingPanelBinding
import com.s3enterprises.jewellowholesale.party.addParty.AddParty
import com.s3enterprises.jewellowholesale.rx.RxBus
import com.s3enterprises.jewellowholesale.rx.RxEvent

class BillingPanelView: LinearLayout {

    lateinit var binding : ViewBillingPanelBinding
    private val autoCompleteTextView by lazy { (binding.nameField.editText as AutoCompleteTextView) }
    private val ic by lazy { autoCompleteTextView.onCreateInputConnection(EditorInfo()) }

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
            if(viewModel.party.value!=null) Utils.hideKeyboard(autoCompleteTextView).also { autoCompleteTextView.clearFocus() }
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

        balanceCash.onTextChanged { if(!viewModel.listenChangeEvents) return@onTextChanged
            viewModel.cashBalance = balanceCash.floatValue.toInt()
            viewModel.calculate()
        }

        balanceFine.onTextChanged { if(!viewModel.listenChangeEvents) return@onTextChanged
            viewModel.fineBalance = balanceFine.floatValue
            viewModel.calculate()
        }

        addGoldLabel.setOnClickListener {
            val goldItem = GoldItem(viewModel.billAndGoldIds++)
            viewModel.goldItemList.add(goldItem)
            val view = GoldItemCardView(context,goldItem)
            goldsContainer.addView(view)
            totalGoldContainer.visibility = View.VISIBLE
        }

        goldRcv1.onTextChanged { if(!viewModel.listenChangeEvents) return@onTextChanged
            viewModel.goldItemList[0].weight = goldRcv1.floatValue
            calculateForFirstGoldItem()
        }

        goldRcvRate1.onTextChanged { if(!viewModel.listenChangeEvents) return@onTextChanged
            viewModel.goldItemList[0].purity = goldRcvRate1.floatValue
            calculateForFirstGoldItem()
        }
        goldRcvRate1.setText(99.5f.toString())
    }

    private fun calculateForFirstGoldItem() {
        val item = binding.model!!.goldItemList[0]
        val c = item.weight * item.purity
        item.fine = (c/100).roundOff(3)
        binding.goldRcvFine1.text = item.fine.toString()
        RxBus.publish(RxEvent.EventBillItemChanged())
    }

    fun setPartiesAdapter(parties: List<Party>) {
        val adapter = AutoCompleteAdapter(context, R.layout.list_item,parties)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) Handler(Looper.getMainLooper()).postDelayed({
                INPUT_CONNECTION = ic
            },100)
            else INPUT_CONNECTION = null
        }
        autoCompleteTextView.setText(" ")
        autoCompleteTextView.setText("")
    }

    fun clear() = with(binding) {
        autoCompleteTextView.setText("")
        cashRcv.setText(""); billChanger.setBillNo(0)
        balanceCash.setText(""); balanceFine.setText("")
        bhavEdit.setText(model!!.goldBhav.toString())
        itemsContainer.removeAllViews(); goldsContainer.removeAllViews()
        goldRcv1.setText(""); goldRcvRate1.setText("")
        goldRcvRate1.setText(99.5f.toString())
        model!!.expanded.value = false
    }

    fun setUpBill(bill: Bill) = with(binding) {

        billChanger.setBillNo(bill.billNo)

        if(model!!.isBillNotFound.value == true) return@with

        autoCompleteTextView.setText(bill.partyName)
        autoCompleteTextView.dismissDropDown()

        itemsContainer.removeAllViews()
        goldsContainer.removeAllViews()

        model?.billItemList?.forEach { billItem ->
            val view = BillItemCardView(context,billItem)
            itemsContainer.addView(view)
        }

        model?.goldItemList?.forEachIndexed { index, goldItem ->
            if(index==0) {
                goldRcv1.setText(goldItem.weight.toString())
                goldRcvRate1.setText(goldItem.purity.toString())
                goldRcvFine1.text = goldItem.fine.toString()
            }
            else {
                val view = GoldItemCardView(context,goldItem)
                goldsContainer.addView(view)
                if(goldItem.fine == 0f) view.removeFocus()
            }
        }
        bhavEdit.setText(bill.bhav.toString())
        cashRcv.setText(bill.cashReceived.toString())
        balanceFine.setText(bill.fineBalance.toString())
        balanceCash.setText(bill.cashBalance.toString())
        binding.model!!.expanded.value = bill.cashBalance!=0 || bill.fineBalance!=0f
        INPUT_CONNECTION = null
    }

    fun removeBalances() = with(binding) {
        balanceCash.setText("")
        balanceFine.setText("")
    }

}