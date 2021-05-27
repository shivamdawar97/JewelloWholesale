package com.s3enterprises.jewellowholesale.billing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.Utils.bhav
import com.s3enterprises.jewellowholesale.Utils.roundOff
import com.s3enterprises.jewellowholesale.Utils.stringToFloat
import com.s3enterprises.jewellowholesale.Utils.stringToInt
import com.s3enterprises.jewellowholesale.bills.BillRepository
import com.s3enterprises.jewellowholesale.database.Converters
import com.s3enterprises.jewellowholesale.database.models.Bill
import com.s3enterprises.jewellowholesale.database.models.BillItem
import com.s3enterprises.jewellowholesale.database.models.Item
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.items.ItemsRepository
import com.s3enterprises.jewellowholesale.party.PartyRepository
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class BillingViewModel:ViewModel() {

    private var billNo = 0
    val lastSavedBill = MutableLiveData<Bill>()
    val items:List<Item>
    get() = ItemsRepository.getSavedItems()!!
    val parties: LiveData<List<Party>>
    get() = PartyRepository.parties
    val isloading = MutableLiveData<Boolean>().apply { value = false }
    var billItemList = ArrayList<BillItem>()
    val itemNamesList = MutableLiveData<List<String>>()
    val party = MutableLiveData<Party>()
    val grossWeight = MutableLiveData<String>().apply { value = "0.0" }
    val fineWeight = MutableLiveData<String>().apply { value = "0.0" }
    val totalAmount = MutableLiveData<String>().apply { value = "0" }
    val goldWeight = MutableLiveData<String>().apply { value = "0.0" }
    val goldPurity = MutableLiveData<String>().apply { value = "99.50" }
    val goldFine = MutableLiveData<String>().apply { value = "0.0" }
    val cashReceived = MutableLiveData<String>().apply { value = "0" }
    val dueFineGold = MutableLiveData<String>().apply { value = "0.0" }
    val dueCash = MutableLiveData<String>().apply { value = "0" }

    init {
        viewModelScope.launch {
            PartyRepository.loadParties()
            itemNamesList.value = ItemsRepository.getItems().map { it.name }
        }
    }

    fun calculate(){
        var gross = 0f; var fine = 0f
        billItemList.forEach {
            gross+=it.weight
            fine+=it.fine
        }
        val tAmount = (fine * bhav).toInt()
        val goldWt = stringToFloat(goldWeight.value!!)
        val goldPr = stringToFloat(goldPurity.value!!)
        val rcdFine = goldWt * goldPr/100
        val cashRvd = if(cashReceived.value.isNullOrBlank()) 0 else cashReceived.value!!.toInt()

        val goldOfCashRvd = if(bhav<1) 0f else cashRvd.toFloat() / bhav
        val totalGoldRvd = rcdFine + goldOfCashRvd
        val dueGold = (fine - totalGoldRvd).roundOff(3)
        val dueAmount = (dueGold * bhav).toInt()

        grossWeight.value =  gross.roundOff(3).toString()
        fineWeight.value = fine.roundOff(3).toString()
        totalAmount.value = tAmount.toString()
        goldFine.value = rcdFine.roundOff(3).toString()
        dueFineGold.value = dueGold.toString()
        dueCash.value = dueAmount.toString()

    }

    fun clearAll() {
        billItemList.clear()
        goldWeight.value = "0.0"
        cashReceived.value = "0"
        goldPurity.value = 99.50.toString()
        calculate()
    }

    fun findParty(typedName: String) {
        party.value = parties.value?.find { it.name == typedName }
    }

    fun saveBill(){
        if(party.value==null) return
        viewModelScope.launch {
            isloading.value = true
            BillRepository.checkForSalesDoc()
            BillRepository.checkForPartyDoc(party.value!!.name)
            lastSavedBill.value = BillRepository.insert(generateBill())
            isloading.value = false
        }
    }

    private fun generateBill() = Bill(
        date = Date().time,
        partyId = party.value!!.pId.toString(),
        partyName = party.value!!.name,
        partyNumber = party.value!!.phoneNumber,
        items = Converters().fromList(billItemList),
        gross =  stringToFloat(grossWeight.value!!) ,
        fine = stringToFloat(fineWeight.value!!),
        bhav = bhav,
        tAmount = stringToInt(totalAmount.value!!),
        goldReceived = stringToFloat(goldWeight.value!!),
        receivedRate = stringToFloat(goldPurity.value!!),
        goldReceivedFine = stringToFloat(goldFine.value!!).roundOff(3),
        cashReceived = stringToInt(cashReceived.value!!),
        dueGold = stringToFloat(dueFineGold.value!!),
        dueAmount = stringToInt(dueCash.value!!)
    )

    fun getPreviousBill() {
        if(billNo==0)
            Utils.KEY_VALUES.addSnapshotListener { value, error ->
                if(error!=null && value!=null)
                viewModelScope.launch {
                    billNo = value["bill_counter"].toString().toInt()
                    val bill = BillRepository.getBill(billNo)
                    setUpBill(bill)
                }

            }
        else viewModelScope.launch {
            billNo -=1;
            val bill = BillRepository.getBill(billNo)
            setUpBill(bill)
        }
    }

    fun getNextBill(){
        viewModelScope.launch {
            billNo +=1;
            val bill = BillRepository.getBill(billNo)
            setUpBill(bill)
        }
    }

    fun getBill(billNo:Int) = viewModelScope.launch{
        val bill = if(billNo == lastSavedBill.value?.billNo) lastSavedBill.value
            else BillRepository.getBill(billNo)
        setUpBill(bill)
    }

    private fun setUpBill(bill: Bill?) {
        if(bill!=null){
            findParty(bill.partyName)
            billItemList = Converters().fromString(bill.items)  as ArrayList<BillItem>

        }
    }

}