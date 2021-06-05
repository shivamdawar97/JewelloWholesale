package com.s3enterprises.jewellowholesale.billing

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.s3enterprises.jewellowholesale.Converter
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.Utils.atEndOfDay
import com.s3enterprises.jewellowholesale.Utils.atStartOfDay
import com.s3enterprises.jewellowholesale.customViews.BillItemCardView
import com.s3enterprises.jewellowholesale.database.Converters
import com.s3enterprises.jewellowholesale.database.models.Bill
import com.s3enterprises.jewellowholesale.database.models.BillItem
import com.s3enterprises.jewellowholesale.databinding.ActivityBillingBinding
import com.s3enterprises.jewellowholesale.pendings.PendingsActivity
import com.s3enterprises.jewellowholesale.print.JewelloBluetoothSocket
import com.s3enterprises.jewellowholesale.rx.RxBus
import com.s3enterprises.jewellowholesale.rx.RxEvent
import com.s3enterprises.jewellowholesale.settings.SettingsActivity
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BillingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBillingBinding
    private val viewModel by viewModels<BillingViewModel>()
    private lateinit var itemsContainer:LinearLayout
    private lateinit var rxBillItemValuesChanged: Disposable
    private lateinit var rxBillItemRemoved: Disposable
    private lateinit var rxBhavChanged: Disposable
    private lateinit var rxOldBillSelected: Disposable
    private lateinit var rxPendingillSelected: Disposable
    private var listenChangeEvents = true
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_billing)
        binding.model = viewModel
        itemsContainer = findViewById(R.id.items_container)
        initializeSetup()
        preferences = getSharedPreferences("jewello_wholesale", Context.MODE_PRIVATE)
        rxBillItemValuesChanged =  RxBus.listen(RxEvent.EventBillItemChanged::class.java)!!.subscribe {
             if(listenChangeEvents) viewModel.calculate()
        }

        rxBhavChanged =  RxBus.listen(RxEvent.BhavUpdated::class.java)!!.subscribe {
             viewModel.calculate()
        }

        rxBillItemRemoved =  RxBus.listen(RxEvent.EventBillItemRemoved::class.java)!!.subscribe { event ->
            viewModel.billItemList = viewModel.billItemList.filter { it.iId!= event.id } as ArrayList<BillItem>
            viewModel.calculate()
        }

        rxOldBillSelected = RxBus.listen(RxEvent.PreviousBillSelected::class.java)!!.subscribe{ event ->
            viewModel.onOldBillSelected(event.bill)
        }

        rxPendingillSelected = RxBus.listen(RxEvent.PendingBillSelected::class.java)!!.subscribe { event ->
            viewModel.onOldBillSelected(event.pending)
        }
    }

    private fun initializeSetup() = with(binding){
        lifecycleOwner = this@BillingActivity
        model = viewModel
        billingPanel.setViewModel(viewModel)
        isItemsListVisible = false

        itemsList.layoutManager = LinearLayoutManager(this@BillingActivity)

        btnSave.setOnClickListener {
            when {
                viewModel.billNo.value!=0 -> {

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
                    else viewModel.saveBill().also { saveBhav() }
                }
                viewModel.party.value==null -> {
                    AlertDialog.Builder(this@BillingActivity)
                        .setTitle("Party not defined")
                        .setMessage("Party name is not provided, Continue as unknown")
                        .setPositiveButton("Yes"){
                                di,_ ->
                            di.dismiss()
                            viewModel.findParty("unknown")
                            viewModel.saveBill().also { saveBhav() }
                        }
                        .setNegativeButton("Cancel"){ di,_->
                            di.dismiss()
                        }
                        .show()
                }
                else -> viewModel.saveBill().also { saveBhav() }
            }
        }

        viewModel.items.observeForever { items ->
            val adapter = ItemsDraggableAdapter(items!!){ i ->
                val billItem = BillItem(i.iId,i.name,rate = i.rate)
                viewModel.billItemList.add(billItem)
                val view = BillItemCardView(this@BillingActivity,billItem)
                itemsContainer.addView(view)
            }
            itemsList.adapter = adapter
            val touchHelper = ItemTouchHelper(adapter.simpleCallback)
            touchHelper.attachToRecyclerView(itemsList)
        }

        viewModel.parties.observeForever {
            billingPanel.setPartiesAdapter(it)
        }

        viewModel.billNo.observeForever {
            billingPanel.setBillNo(it)
        }

        viewModel.party.observeForever {
            if(viewModel.billNo.value!=0) {
                billingPanel.setPartyName(it.name)
                itemsContainer.removeAllViews()
                listenChangeEvents = false
                viewModel.billItemList.forEach { billItem ->
                    val view = BillItemCardView(this@BillingActivity,billItem)
                    itemsContainer.addView(view)
                }
                listenChangeEvents = true
            }
        }

        printBtn?.setOnClickListener {
            val printBill = viewModel.generateBillPrint()
            if(printBill!=null) lifecycleScope.launch {
                JewelloBluetoothSocket.printData(printBill,this@BillingActivity)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.billing_options_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.reset -> resetBill()
            R.id.settings -> startActivity(Intent(this,SettingsActivity::class.java))
            R.id.send_pending -> {
                val converter = Converters()
                val listInString = preferences.getString("pending","")
                val list = if(listInString.isNullOrBlank()) ArrayList() else converter.fromStringToBills(listInString)
                val bill = viewModel.generateBill()
                list?.add(bill)
                val stringOfList = converter.fromListToString(list)
                preferences.edit().putString("pending",stringOfList).apply()
                resetBill()
            }
            R.id.view_pending -> startActivity(Intent(this,PendingsActivity::class.java))
        }
        return true
    }

    private fun resetBill(){
        itemsContainer.removeAllViews()
        viewModel.clearAll()
        binding.billingPanel.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        rxBillItemValuesChanged.dispose()
        rxBhavChanged.dispose()
        rxBillItemRemoved.dispose()
        rxOldBillSelected.dispose()
    }

    private fun saveBhav(){
        if(!viewModel.goldBhav.value.isNullOrBlank() &&  viewModel.goldBhav.value != "0")
        preferences.edit().putString("printer_name",viewModel.goldBhav.value).apply()
    }
}