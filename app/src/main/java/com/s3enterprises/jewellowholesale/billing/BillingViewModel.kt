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
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.text.StringBuilder

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val billRepository: BillRepository,
    private val itemsRepository: ItemsRepository,
    private val partyRepository: PartyRepository
):ViewModel() {

    var listenChangeEvents = true
    val loadedBill = MutableLiveData<Bill?>()
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
        val cashBh = (fineBh * goldBhav).toInt()

        val fineCr = if(goldBhav<1) 0f else cashReceived.toFloat() / goldBhav

        val fineDu = (fineBh-fineCr).roundOff(3)

        val cashDu = cashBh - cashReceived

        grossGS.value =  grossGs.roundOff(3)
        fineGS.value = fineGs.roundOff(3)

        grossGR.value =  grossGr.roundOff(3)
        fineGR.value = fineGr.roundOff(3)

        cashBH.value = cashBh

        fineBH.value = fineBh.roundOff(3)
        fineCR.value = fineCr.roundOff(3)
        fineDU.value = fineDu
        cashDU.value = cashDu

    }

    fun clearAll() {
        isBillNotFound.value = false
        billItemList.clear(); goldItemList.clear()
        cashReceived = 0 ; party.value = null
        calculate()
    }

    fun findParty(typedName: String) {
        party.value = parties.value?.find { it.name == typedName }
    }

    fun saveBill() { // Or update bill
        calculate()
        if(loadedBill.value == null || loadedBill.value?.billNo?:0 == 0) viewModelScope.launch {
            isLoading.value = true
            billCounter = if(billCounter!=2000) billCounter+1 else 1
            val newBill = generateBill()
            billRepository.insert(newBill).toInt()
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

    fun generateBillPrint2() = loadedBill.value?.let{
        val tab = "     "
        val stringBuilder = StringBuilder()
            .append("            Estimation\n")
            .append("Bill no: ${it.billNo}${tab}${Utils.getDate(it.date)}\n")
            .append("Party: ${it.partyName}\n")
            .append("----------------------------------------\n")
            billItemList.forEach { i ->
                var det = 0
                stringBuilder.append(i.name.apply { det+=length })
                .append(tab(14-det).apply { det+=length })
                .append("${i.weight}  x  ${i.rate}".apply { det+=length })
                .append(tab(32-det))
                .append("${i.fine}\n")
            }
        stringBuilder.append("----------------------------------------\n")

        var det1 = 0
        stringBuilder.append("GS".apply { det1+=length })
            .append(tab(14-det1).apply { det1+=length })
            .append("${grossGS.value}".apply { det1+=length })
            .append(tab(32-det1))
            .append("${fineGS.value}\n\n")

        goldItemList.forEachIndexed  { pos, i ->
            var det = 0
            if(pos==0) stringBuilder.append("GR".apply { det+=length })
            stringBuilder.append(tab(14-det).apply { det+=length })
                .append("${i.weight} x ${i.purity}".apply { det+=length })
                .append(tab(32-det))
                .append("${i.fine}\n")
        }
        stringBuilder.append("----------------------------------------\n")
        if(goldItemList.size>1) {
            var det = 0
            stringBuilder.append("GRT".apply { det+=length })
//                .append(tab(14-det).apply { det+=length })
//                .append("${grossGR.value}".apply { det+=length })
                .append(tab(32-det))
                .append("${fineGR.value}\n")
        }

        var det2 = 0
        stringBuilder.append("BH ${it.bhav}".apply { det2+=length })
            .append(tab(14-det2).apply { det2+=length })
            .append(Utils.getRupeesFormatted(cashBH.value?:0).apply { det2+=length })
            .append(tab(32-det2))
            .append("${fineBH.value ?: 0f}\n")

        var det3 = 0
        stringBuilder.append("CR".apply { det3+=length })
            .append(tab(14-det3).apply { det3+=length })
            .append(Utils.getRupeesFormatted(it.cashReceived).apply { det3+=length })
            .append(tab(32-det3))
            .append("${fineCR.value ?: 0f}\n")

        var det4 = 0
        stringBuilder.append("DU".apply { det4+=length })
            .append(tab(14-det4).apply { det4+=length })
            .append(Utils.getRupeesFormatted(cashDU.value?:0).apply { det4+=length })
            .append(tab(32-det4))
            .append("${fineDU.value ?: 0f}\n\n")

        .append("_____________Thank you____________\n\n\n\n").toString()
    }


    fun generateBill(oldBill: Bill?=null):Bill {
        val bill = oldBill?:Bill(
            billNo = billCounter,
            date = Date().time,
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

    fun getPreviousBill() { when {
            loadedBill.value == null -> if(billCounter!=0) getBill(billCounter) else return
            loadedBill.value!!.billNo == 1 -> return
            else -> getBill(loadedBill.value!!.billNo-1)
        }
    }

    fun getNextBill(){ when{
            loadedBill.value == null -> return
            loadedBill.value!!.billNo == billCounter -> loadedBill.value = null
            else -> getBill(loadedBill.value!!.billNo+1)
        }
    }

    private fun getBill(no:Int) = viewModelScope.launch {
        isLoading.value = true
        val bill = billRepository.getBill(no)
        if(bill!=null) setUpBill(bill)
        else {
            isBillNotFound.value = true
            loadedBill.value = Bill(billNo = no)
        }
        isLoading.value = false
    }

    fun setUpBill(bill: Bill)  {

        billItemList.clear(); billItemList.addAll(Converters().fromString(bill.items)!!)
        goldItemList.clear(); goldItemList.addAll(Converters().fromString1(bill.golds)!!)
        isBillNotFound.value = false
        goldBhav = bill.bhav
        cashReceived = bill.cashReceived
        party.value = Party(name=bill.partyName, phoneNumber = bill.partyNumber)
        calculate()
        loadedBill.value = bill
    }

    fun updateItemsPositions(updatedList: List<Item>) = viewModelScope.launch {
        itemsRepository.updateAll(updatedList)
    }

    fun deleteBill() = viewModelScope.launch {
        billRepository.deleteBill(loadedBill.value!!)
        loadedBill.value = null
    }

    fun tab(size:Int) :String {
        val s = StringBuilder()
        for(i in 0..size) s.append(" ")
        return s.toString()
    }

}