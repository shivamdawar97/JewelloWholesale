package com.s3enterprises.jewellowholesale.billing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s3enterprises.jewellowholesale.Utils
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

    val billNo = MutableLiveData<Int>().apply { value = 0 }
    val lastSavedBill = MutableLiveData<Bill>()
    val items:LiveData<List<Item>>
    get() = ItemsRepository.items
    val parties: LiveData<List<Party>>
    get() = PartyRepository.parties
    val isloading = MutableLiveData<Boolean>().apply { value = false }
    val isBillLoading = MutableLiveData<Boolean>().apply { value = false }
    val isBillNotFound = MutableLiveData<Boolean>().apply { value = false }
    var billItemList = ArrayList<BillItem>()
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
    val goldBhav = MutableLiveData<String>().apply { value = "0" }
    private var previousBill:Bill? = null

    init {
        PartyRepository.loadParties()
        ItemsRepository.loadItems()
    }

    fun calculate(){
        var gross = 0f; var fine = 0f
        billItemList.forEach {
            gross+=it.weight
            fine+=it.fine
        }
        val bhav = stringToInt(goldBhav.value!!)
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
        billNo.value = 0
        isBillNotFound.value = false
        billItemList.clear()
        goldWeight.value = "0.0"
        cashReceived.value = "0"
        goldPurity.value = 99.50.toString()
        calculate()
    }

    fun findParty(typedName: String) {
        party.value = parties.value?.find { it.name == typedName }
    }

    fun saveBill(){ // Or update bill
        if(party.value==null) return
        if(billNo.value == 0) viewModelScope.launch {
            isloading.value = true
            BillRepository.checkForSalesDoc()
            BillRepository.checkForPartyDoc(party.value!!.name)
            lastSavedBill.value = BillRepository.insert(generateBill())
            previousBill = lastSavedBill.value
            isloading.value = false
        }
        else if(!isBillNotFound.value!!) viewModelScope.launch {
            isloading.value = true
            previousBill = BillRepository.update(generateBill(),previousBill!!)
            lastSavedBill.value = previousBill
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
        bhav = stringToInt(goldBhav.value!!),
        tAmount = stringToInt(totalAmount.value!!),
        goldReceived = stringToFloat(goldWeight.value!!),
        receivedRate = stringToFloat(goldPurity.value!!),
        goldReceivedFine = stringToFloat(goldFine.value!!).roundOff(3),
        cashReceived = stringToInt(cashReceived.value!!),
        dueGold = stringToFloat(dueFineGold.value!!),
        dueAmount = stringToInt(dueCash.value!!)
    )

    fun getPreviousBill() {
        if(billNo.value==0)
            Utils.KEY_VALUES.addSnapshotListener { value, error ->
                if(error==null && value!=null)
                {
                    billNo.value = value["bill_counter"].toString().toInt()
                    getBill()
                }
            }
        else {
            billNo.value = billNo.value!!.minus(1)
            getBill()
        }
    }

    fun getNextBill(){
        Utils.KEY_VALUES.addSnapshotListener { value, error ->
            if(error==null && value!=null && billNo.value != value["bill_counter"].toString().toInt())
            {
                billNo.value = billNo.value!!.plus(1)
                getBill()
            }

        }
    }

    private fun getBill() = viewModelScope.launch {
        isBillLoading.value = true
        previousBill = if(billNo.value!! == lastSavedBill.value?.billNo) lastSavedBill.value
            else BillRepository.getBill(billNo.value!!)
        if(previousBill!=null)
        setUpBill()
        else isBillNotFound.value = true
        isBillLoading.value = false
    }

    private fun setUpBill() = previousBill?.let {
        billItemList = Converters().fromString(it.items)  as ArrayList<BillItem>
        isBillNotFound.value = false
        findParty(it.partyName)
        goldBhav.value = it.bhav.toString()
        cashReceived.value = it.cashReceived.toString()
        goldWeight.value = it.goldReceived.toString()
        goldPurity.value = it.receivedRate.toString()
        grossWeight.value = it.gross.toString()
        fineWeight.value = it.fine.toString()
        totalAmount.value = it.tAmount.toString()
        goldFine.value = it.goldReceivedFine.toString()
        dueFineGold.value = it.dueGold.toString()
        dueCash.value = it.dueAmount.toString()
    }

}