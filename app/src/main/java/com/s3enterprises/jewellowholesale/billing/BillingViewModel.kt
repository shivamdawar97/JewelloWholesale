package com.s3enterprises.jewellowholesale.billing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.Utils.roundOff
import com.s3enterprises.jewellowholesale.bills.BillRepository
import com.s3enterprises.jewellowholesale.database.Converters
import com.s3enterprises.jewellowholesale.database.models.*
import com.s3enterprises.jewellowholesale.items.ItemsRepository
import com.s3enterprises.jewellowholesale.party.PartyRepository
import com.s3enterprises.jewellowholesale.rx.RxBus
import com.s3enterprises.jewellowholesale.rx.RxEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val billRepository: BillRepository,
    private val itemsRepository: ItemsRepository,
    private val partyRepository: PartyRepository
):ViewModel() {

    val billNo = MutableLiveData<Int>().apply { value = 0 }
    val loadedBill = MutableLiveData<Bill>()
    val items:LiveData<List<Item>>
    get() = itemsRepository.items
    val parties: LiveData<List<Party>>
    get() = partyRepository.parties

    val party = MutableLiveData<Party>()

    val isLoading = MutableLiveData<Boolean>().apply { value = false }
    val isBillNotFound = MutableLiveData<Boolean>().apply { value = false }

    val billItemList = ArrayList<BillItem>()
    val grossGS = MutableLiveData<Float>().apply { value = 0f }
    val fineGS = MutableLiveData<Float>().apply { value = 0f }

    val goldItemList = ArrayList<GoldItem>()
    val grossGR = MutableLiveData<Float>().apply { value = 0f }
    val fineGR = MutableLiveData<Float>().apply { value = 0f }

    val fineBH = MutableLiveData<Float>().apply { value = 0f }
    val fineCR = MutableLiveData<Float>().apply { value = 0f }

    val cashBH = MutableLiveData<Int>().apply { value = 0 }

    var cashReceived = 0
    val fineDU = MutableLiveData<Float>().apply { value = 0f }
    val cashDU = MutableLiveData<Int>().apply { value = 0 }
    var goldBhav = 0
    var billCounter = 0

    fun calculate(){
        var grossGs = 0f; var fineGs = 0f ;var grossGr = 0f; var fineGr = 0f
        billItemList.forEach {
            grossGs+=it.weight
            fineGs+=it.fine
        }

        goldItemList.forEach {
            grossGr+=it.weight
            fineGr+=it.fine
        }

        val fineBh = fineGs - fineGr
        val tAmount = (fineBh * goldBhav).toInt()

        val fineCr = if(goldBhav<1) 0f else cashReceived.toFloat() / goldBhav

        val fineDu = (fineBh-fineCr).roundOff(3)

        val cashDu = tAmount - cashReceived

        grossGS.value =  grossGs.roundOff(3)
        fineGS.value = fineGs.roundOff(3)

        grossGR.value =  grossGr.roundOff(3)
        fineGR.value = fineGr.roundOff(3)

        cashBH.value = tAmount

        fineBH.value = fineBh.roundOff(3)
        fineCR.value = fineCr.roundOff(3)
        fineDU.value = fineDu
        cashDU.value = cashDu

    }

    fun clearAll() {
        billNo.value = 0; isBillNotFound.value = false
        billItemList.clear(); goldItemList.clear()
        cashReceived = 0
        calculate()
    }

    fun findParty(typedName: String) {
        party.value = parties.value?.find { it.name == typedName }
    }

    fun saveBill() { // Or update bill
        calculate()
        if(billNo.value == 0) viewModelScope.launch {
            isLoading.value = true
            billCounter = if(billCounter!=5) billCounter+1 else 1
            val newBill = generateBill()
            billNo.value = billRepository.insert(newBill).toInt()
            RxBus.publish(RxEvent.PreferencesUpdated())
            loadedBill.value = newBill
            isLoading.value = false
        }
        else if(!isBillNotFound.value!!) viewModelScope.launch {
            isLoading.value = true
            val updatedBill = generateBill(loadedBill.value!!)
            billRepository.update(updatedBill)
            loadedBill.value = updatedBill
            isLoading.value = false
        }
    }

    fun generateBillPrint() = loadedBill.value?.let{
        val stringBuilder = StringBuilder()
            .append("\n Bill Estimation\n")
            .append("Bill no: ${it.billNo} \tDate:${Utils.getDate(it.date)}")
            .append("Party: ${it.partyName}\t Number: ${it.partyNumber}")
            .append("\n----------------------------\n")
            billItemList.forEach { i ->
                stringBuilder.append("${i.name}\n")
                    .append("${i.weight} * ${i.rate} = ${i.fine}")
            }
            stringBuilder.append("\n----------------------------\n")
                .append("GS \t${grossGS.value}\t\t\t ${fineGS.value}")

        if(fineGR.value!!.toFloat()!=0f)
            goldItemList.forEach { i ->
                stringBuilder.append("${i.weight} * ${i.purity} = ${i.fine}")
            }
        stringBuilder.append("\n----------------------------\n")
            .append("GR \t${grossGR.value}}\t\t\t ${fineGR.value}")
            .append("CR ${it.bhav}")
            .append("Total Amount ${cashBH.value}")
        if(it.cashReceived!=0)
            stringBuilder.append("Amount received: ${it.cashReceived}")
        stringBuilder.append("Due Gold: ${fineDU.value}")
        stringBuilder.append("Due Amount: ${cashDU.value}").toString()
    }


    fun generateBill(oldBill: Bill?=null):Bill {
        val bill = oldBill?:Bill(
            billNo = billCounter,
            date = Date().time,
            partyId = if(party.value==null) "N/A" else party.value!!.pId.toString(),
            partyName = if(party.value==null) "N/A" else party.value!!.name,
            partyNumber = if(party.value==null) "N/A" else party.value!!.phoneNumber ,
        )
        with(bill){
            items = Converters().fromList(billItemList)
            golds = Converters().fromList(goldItemList)
            bhav = goldBhav
            cashReceived = this@BillingViewModel.cashReceived
            cashDu = cashDU.value!!
        }
        return bill
    }

    fun getPreviousBill() {
        if(billNo.value==1) billNo.value = billCounter
        else billNo.value = billNo.value!!.minus(1)
        getBill()
    }

    fun getNextBill(){
        if(billNo.value == billCounter) billNo.value = 1
        else billNo.value = billNo.value!!.plus(1)
        getBill()
    }

    private fun getBill() = viewModelScope.launch {
        isLoading.value = true
        loadedBill.value = billRepository.getBill(billNo.value!!)
        if(loadedBill.value!=null) setUpBill()
        else isBillNotFound.value = true
        isLoading.value = false
    }

    private fun setUpBill() = loadedBill.value?.let {

        billItemList.clear(); billItemList.addAll(Converters().fromString(it.items)!!)
        goldItemList.clear(); goldItemList.addAll(Converters().fromString1(it.golds)!!)
        isBillNotFound.value = false
        findParty(it.partyName)
        goldBhav = it.bhav
        cashReceived = it.cashReceived
        calculate()
    }

    fun onOldBillSelected(bill: Bill) {
        loadedBill.value = bill ; billNo.value = bill.billNo
        setUpBill()
    }

    fun updateItemsPositions(updatedList: List<Item>) = viewModelScope.launch {
        itemsRepository.updateAll(updatedList)
    }

}