package com.s3enterprises.jewellowholesale.billing

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.Utils.atEndOfDay
import com.s3enterprises.jewellowholesale.Utils.atStartOfDay
import com.s3enterprises.jewellowholesale.Utils.getTextOr
import com.s3enterprises.jewellowholesale.customViews.BillItemCardView
import com.s3enterprises.jewellowholesale.database.models.BillItem
import com.s3enterprises.jewellowholesale.database.models.Item
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.databinding.ActivityBillingBinding
import com.s3enterprises.jewellowholesale.items.addItem.AddItem
import com.s3enterprises.jewellowholesale.party.PartyRepository
import com.s3enterprises.jewellowholesale.print.JewelloBluetoothSocket
import com.s3enterprises.jewellowholesale.rx.RxBus
import com.s3enterprises.jewellowholesale.rx.RxEvent
import com.s3enterprises.jewellowholesale.sales.SalesActivity
import com.s3enterprises.jewellowholesale.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BillingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBillingBinding
    private val viewModel by viewModels<BillingViewModel>()
    private lateinit var rxBillItemValuesChanged: Disposable
    private lateinit var rxBillItemRemoved: Disposable
    private lateinit var rxGoldItemRemoved: Disposable
    private lateinit var rxBhavChanged: Disposable
    private lateinit var rxOldBillSelected: Disposable
    @Inject lateinit var preferences: SharedPreferences
    @Inject lateinit var partiesRepository: PartyRepository
    private var touchHelper: ItemTouchHelper? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.apply{
            setDisplayShowHomeEnabled(true)
            setLogo(R.drawable.ic_diamond)
            setDisplayUseLogoEnabled(true)
        }

        binding = DataBindingUtil.setContentView(this,R.layout.activity_billing)
        binding.model = viewModel

        getPreferences()
        initializeSetup()

        rxBillItemValuesChanged =  RxBus.listen(RxEvent.EventBillItemChanged::class.java)!!.subscribe {
             if(viewModel.listenChangeEvents) viewModel.calculate()
        }

        rxBhavChanged = RxBus.listen(RxEvent.PreferencesUpdated::class.java)!!.subscribe {
            savePreferences()
        }

        rxBillItemRemoved =  RxBus.listen(RxEvent.EventBillItemRemoved::class.java)!!.subscribe { event ->
            val newList = viewModel.billItemList.filter { it.iId != event.id }
            viewModel.billItemList.clear()
            viewModel.billItemList.addAll(newList)
            viewModel.calculate()
        }
        rxGoldItemRemoved =  RxBus.listen(RxEvent.EventGoldItemRemoved::class.java)!!.subscribe { event ->
            val newList = viewModel.goldItemList.filter { it.id != event.id }
            viewModel.goldItemList.clear()
            viewModel.goldItemList.addAll(newList)
            viewModel.calculate()
            if(newList.size == 1) binding.billingPanel.binding.totalGoldContainer.visibility = View.GONE
        }

        rxOldBillSelected = RxBus.listen(RxEvent.PreviousBillSelected::class.java)!!.subscribe{ event ->
            viewModel.setUpBill(event.bill)
        }

        viewModel.expanded.observeForever {
            if(!it) binding.billingPanel.removeBalances()
        }

    }

    private fun checkPartyAndSaveBill() = lifecycleScope.launch {
        if (viewModel.party.value == null) {
            val name = binding.billingPanel.binding.nameField.editText?.text?.toString()
            val phoneNumber = binding.billingPanel.binding.partyNumber.text?.toString()
            if (name.isNullOrBlank()) {
                viewModel.saveBill(); return@launch
            }
            val newParty = Party(
                name = name,
                phoneNumber = phoneNumber ?: ""
            )
            partiesRepository.insert(newParty)
            viewModel.setParty(newParty)
        }
        viewModel.saveBill()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeSetup() = with(binding){
        lifecycleOwner = this@BillingActivity
        model = viewModel
        billingPanel.setViewModel(viewModel)

        itemsList.layoutManager = LinearLayoutManager(this@BillingActivity)

        btnSave.setOnClickListener {
            when {
                viewModel.loadedBill.value != null -> {

                    val billDate = viewModel.loadedBill.value!!.date
                    val thisDate = Date()
                    val todayRange = atStartOfDay(thisDate).time..atEndOfDay(thisDate).time

                    if(billDate !in todayRange)
                        AlertDialog.Builder(this@BillingActivity)
                            .setTitle("Old date bill")
                            .setMessage("Bill of previous date are not allowed to be edit")
                            .setPositiveButton("Ok"){
                                    di,_ ->
                                di.dismiss()
                            }
                            .show()
                    else checkPartyAndSaveBill()
                }

                else -> checkPartyAndSaveBill()
            }
        }


        viewModel.items.observeForever { items ->
            val buttonList = arrayListOf(Item(0,"Add Item")).apply { addAll(items) }
            touchHelper?.attachToRecyclerView(null)
            val adapter = ItemsDraggableAdapter(
                list =  buttonList,
                onSelected =  { i ->
                if(i.position == 0)
                    startActivity(Intent(this@BillingActivity,AddItem::class.java))
                else {
                    val billItem = BillItem(viewModel.billAndGoldIds++,
                        i.name, rate = i.rate, isStone = i.isStone)
                    viewModel.billItemList.add(billItem)
                    val view = BillItemCardView(this@BillingActivity, billItem)
                    billingPanel.binding.itemsContainer.addView(view)
                }},
                onItemPositionsChanged =  { positionsChangedList ->
                viewModel.updateItemsPositions(positionsChangedList)
            })
            itemsList.adapter = adapter
            touchHelper = ItemTouchHelper(adapter.generateSimpleCallback())
            touchHelper?.attachToRecyclerView(itemsList)

        }

        viewModel.parties.observeForever {
            billingPanel.setPartiesAdapter(it)
        }

        viewModel.loadedBill.observe(this@BillingActivity) { bill ->
            if(bill!=null) {
                viewModel.listenChangeEvents = false

                billingPanel.setUpBill(bill)

                viewModel.listenChangeEvents = true
            }
            else resetBill()
        }

        btnPrint.setOnClickListener {
            lifecycleScope.launch {
                val socket = JewelloBluetoothSocket()
                socket.findDeviceAndConnect(this@BillingActivity)
                socket.printData(viewModel.generateBillPrint1()!!)
                socket.printBoldData(viewModel.generateBillPrint2()!!)
                socket.printData(viewModel.generateBillPrint3()!!)
                socket.printBoldData(viewModel.generateBillPrint4())
                socket.printData(viewModel.generateBillPrint5())
                socket.disconnectBT()
            }
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(this@BillingActivity)
                .setCustomTitle(TextView(this@BillingActivity).apply {
                    text = "Delete Bill"
                    setTextColor(Color.RED)
                    textSize = 60f
                    setTypeface(null, Typeface.BOLD)
                })
                .setMessage("Are you sure, you want to delete this bill")
                .setPositiveButton("Yes"){
                        di,_ ->
                    di.dismiss()

                    val billDate = viewModel.loadedBill.value!!.date
                    val thisDate = Date()
                    val todayRange = atStartOfDay(thisDate).time..atEndOfDay(thisDate).time

                    if(billDate !in todayRange)
                        AlertDialog.Builder(this@BillingActivity)
                            .setTitle("Old date bill")
                            .setMessage("Bill of previous date are not allowed to be deleted")
                            .setPositiveButton("Ok"){
                                    di2,_ ->
                                di2.dismiss()
                            }
                            .show()
                    else {
                        viewModel.deleteBill()
                        resetBill()
                    }

                }
                .setNegativeButton("Cancel"){ di,_->
                    di.dismiss()
                }
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.billing_options_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.reset -> viewModel.loadedBill.value = null
            R.id.settings -> startActivity(Intent(this,SettingsActivity::class.java))
            R.id.receipt -> lifecycleScope.launch {
                val printBill = viewModel.generateSamplePrint()
                val socket = JewelloBluetoothSocket().apply { findDeviceAndConnect(this@BillingActivity) }
                socket.printData(printBill)
                socket.disconnectBT()
            }
            R.id.expanded -> {
                item.isChecked = !item.isChecked
                viewModel.expanded.value = item.isChecked
            }
            R.id.sales -> {
                startActivity(Intent(this, SalesActivity::class.java))
            }

        }
        return true
    }

    private fun resetBill(){
        viewModel.clearAll()
        viewModel.goldBhav = preferences.getInt("bhav",0)
        binding.billingPanel.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        rxBillItemValuesChanged.dispose()
        rxBhavChanged.dispose()
        rxBillItemRemoved.dispose()
        rxOldBillSelected.dispose()
    }

    private fun getPreferences() {
        viewModel.goldBhav = preferences.getInt("bhav",0)
        viewModel.billCounter = preferences.getInt("bill_counter",0)
        Utils.printerName = preferences.getString("printer_name","")!!
        binding.billingPanel.binding.counterLabel.text = viewModel.billCounter.toString()
    }

    private fun savePreferences(){
        preferences.edit().putInt("bhav",viewModel.goldBhav).apply()
        preferences.edit().putInt("bill_counter",viewModel.billCounter).apply()
        binding.billingPanel.binding.counterLabel.text = viewModel.billCounter.toString()
    }
}