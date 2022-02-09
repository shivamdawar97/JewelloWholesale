package com.s3enterprises.jewellowholesale.billing

import android.text.Html
import android.util.Log
import androidx.core.text.HtmlCompat
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
import com.s3enterprises.jewellowholesale.sales.SalesRepository
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
    private val partyRepository: PartyRepository,
    private val salesRepository: SalesRepository
):ViewModel() {

    private var det4 = 0
    var listenChangeEvents = true
    val loadedBill = MutableLiveData<Bill?>()
    val isBillSaved = MutableLiveData<Boolean>().apply { value = false }
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
    var billAndGoldIds = 0

    fun calculate(){
        var grossGs = 0f; var fineGs = 0f ;var grossGr = 0f; var fineGr = 0f
        billItemList.forEach {
            if(it.weight>0) grossGs+=it.weight
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

        isBillSaved.value = false
    }

    fun clearAll() {
        isBillNotFound.value = false
        billItemList.clear(); goldItemList.clear()
        cashReceived = 0 ; party.value = null
        billAndGoldIds = 0
        calculate()
    }

    fun findParty(typedName: String) {
        party.value = parties.value?.find { it.name == typedName }
    }

    fun saveBill() { // Or update bill
        calculate()
        if(loadedBill.value == null || loadedBill.value?.billNo?:0 == 0) viewModelScope.launch {
            isLoading.value = true
            billCounter = if(billCounter!=10000) billCounter+1 else 1
            val newBill = generateBill()
            billRepository.insert(newBill)
            RxBus.publish(RxEvent.PreferencesUpdated())
            loadedBill.value = newBill
            isLoading.value = false
            isBillSaved.value = true
            updateSales()
        }
        else if(!isBillNotFound.value!!) viewModelScope.launch {
            isLoading.value = true
            val updatedBill = generateBill(loadedBill.value!!)
            billRepository.update(updatedBill)
            updateSales(updatedBill)
            loadedBill.value = updatedBill
            isLoading.value = false
            isBillSaved.value = true
        }
    }

    private suspend fun updateSales(updatedBill:Bill?=null,isDelete:Boolean=false) = loadedBill.value!!.let {
        when {
            updatedBill!=null -> {
                val cash = (updatedBill.cashReceived + updatedBill.cashDu) - (it.cashReceived+it.cashDu)
                var gold = 0f
                Converters().fromString1(updatedBill.golds)?.forEach { gIt ->
                    if(gIt.purity>=99f) gold+=gIt.weight
                }
                goldItemList.forEach { gIt ->
                    if(gIt.purity>=99f) gold-=gIt.weight
                }
                val total = (updatedBill.fineGs * updatedBill.bhav) - (it.fineGs * it.bhav)
                Log.i("Sales_Update","$cash $gold ${total.toInt()}")
                salesRepository.updateTodaySale(cash,gold,total.toInt())
            }
            isDelete -> {
                val cash =  it.cashReceived + it.cashDu
                var gold = 0f
                goldItemList.forEach { gIt ->
                    if(gIt.purity>=99f) gold+=gIt.weight
                }
                val total = it.fineGs * it.bhav
                Log.i("Sales_Update","$cash $gold ${total.toInt()}")
                salesRepository.updateTodaySale(-cash,-gold,-total.toInt())
            }
            else -> {
                val cash =  it.cashReceived + it.cashDu
                var gold = 0f
                goldItemList.forEach { gIt ->
                    if(gIt.purity>=99f) gold+=gIt.weight
                }
                val total = it.fineGs * it.bhav
                Log.i("Sales_Update","$cash $gold ${total.toInt()}")
                salesRepository.updateTodaySale(cash,gold,total.toInt())
            }
        }

    }

    fun generateSamplePrint():String {
        val stringBuilder = StringBuilder()
        billItemList.forEach { i ->
            var det = 0
            stringBuilder.append(i.name.apply { det+=length })
                .append(tab(16-det).apply { det+=length })
                .append("${i.weight}".apply { det+=length })
                .append(tab(23-det).apply { det+=length })
                .append("x".apply { det+=length })
                .append(tab(25-det).apply { det+=length })
                .append("${i.rate}".apply { det+=length })
                .append(tab(31-det).apply { det+=length })
                .append("=".apply { det+=length })
                .append(tab(34-det))
                .append("${i.fine}\n")
        }
        stringBuilder.append("-----------------------------------------------\n")
        var det1 = 0
        stringBuilder.append("Total".apply { det1+=length })
            .append(tab(16-det1).apply { det1+=length })
            .append("${grossGS.value}".apply { det1+=length })
            .append(tab(34-det1))
            .append("${fineGS.value}\n\n\n\n")

        return stringBuilder.toString()
    }

    fun generateBillPrint1() = loadedBill.value?.let{
        val stringBuilder = StringBuilder()
            .append(tab(14))
            .append("Estimate\n")
            .append("Bill no: ${it.billNo}${tab(12)}${Utils.getDate(it.date)}\n")
            .append("Party: ${it.partyName}\n")
            .append("-----------------------------------------------\n")
        billItemList.forEach { i ->
            var det = 0
            if(i.weight>0) stringBuilder.append(i.name.apply { det+=length })
                .append(tab(16-det).apply { det+=length })
                .append("${i.weight}".apply { det+=length })
                .append(tab(23-det).apply { det+=length })
                .append("x".apply { det+=length })
                .append(tab(25-det).apply { det+=length })
                .append("${i.rate}".apply { det+=length })
                .append(tab(31-det).apply { det+=length })
                .append("=".apply { det+=length })
                .append(tab(34-det))
                .append("${i.fine}\n")
        }
        stringBuilder.append("-----------------------------------------------\n")

        val returns = billItemList.filter { i -> i.weight<0 }
        if(returns.isNotEmpty()) {
            stringBuilder.append("Return/Wapasi\n")
            returns.forEach { i ->
                var det = 0
                stringBuilder.append(i.name.apply { det+=length })
                    .append(tab(15-det).apply { det+=length })
                    .append("${i.weight}".apply { det+=length })
                    .append(tab(23-det).apply { det+=length })
                    .append("x".apply { det+=length })
                    .append(tab(25-det).apply { det+=length })
                    .append("${i.rate}".apply { det+=length })
                    .append(tab(31-det).apply { det+=length })
                    .append("=".apply { det+=length })
                    .append(tab(33-det))
                    .append("${i.fine}\n")
            }
            stringBuilder.append("-----------------------------------------------\n")
        }


        var det1 = 0
        stringBuilder.append("Total".apply { det1+=length })
            .append(tab(16-det1).apply { det1+=length })
            .append("${grossGS.value}".apply { det1+=length })
            .append(tab(34-det1))
            .append("${fineGS.value}\n\n")

        goldItemList.forEachIndexed  { pos, i ->
            var det = 0
            if(pos==0) stringBuilder.append("Gold Received".apply { det+=length })
            stringBuilder.append(tab(16-det).apply { det+=length })
                .append("${i.weight}".apply { det+=length })
                .append(tab(23-det).apply { det+=length })
                .append("x".apply { det+=length })
                .append(tab(25-det).apply { det+=length })
                .append("${i.purity}".apply { det+=length })
                .append(tab(31-det).apply { det+=length })
                .append("=".apply { det+=length })
                .append(tab(34-det))
                .append("${i.fine}\n")
        }
        stringBuilder.append("-----------------------------------------------\n")
        if(goldItemList.size>1) {
            var det = 0
            stringBuilder.append("Total".apply { det+=length })
                .append(tab(34-det))
                .append("${fineGR.value}\n")
        }
        return@let stringBuilder.toString()
    }

    fun generateBillPrint2() = loadedBill.value?.let {
        return@let "BHAV ${it.bhav}"
    }

    fun generateBillPrint3() = loadedBill.value?.let{
        var det2 = "BHAV ${it.bhav}".length
        val stringBuilder = StringBuilder()
            .append(tab(16-det2).apply { det2+=length })
            .append(Utils.getRupeesFormatted(cashBH.value?:0).apply { det2+=length })
            .append(tab(34-det2))
            .append("${fineBH.value ?: 0f}\n")

        var det3 = 0
        stringBuilder.append("Cash Received".apply { det3+=length })
            .append(tab(16-det3).apply { det3+=length })
            .append(Utils.getRupeesFormatted(it.cashReceived).apply { det3+=length })
            .append(tab(34-det3))
            .append("${fineCR.value ?: 0f}\n")

        det4 = 0
        stringBuilder.append("Due".apply { det4+=length })
            .append(tab(16-det4).apply { det4+=length }).toString()
    }

    fun generateBillPrint4() = loadedBill.value?.let {
        StringBuilder().append(Utils.getRupeesFormatted(it.cashDu).apply { det4+=length })
        .append(tab(34-det4))
        .append("${fineDU.value ?: 0f}\n\n\n").toString()
    }

    fun generateBillPrint5() = loadedBill.value?.let {
        StringBuilder().append("Gross Weight:\n\n\n")
        .append("_________________Thank you________________\n\n\n\n").toString()
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
            fineGs = fineGS.value!!
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
        billAndGoldIds = billItemList[0].iId
        billItemList.forEach { if(it.iId>billAndGoldIds) billAndGoldIds = it.iId }
        goldItemList.forEach { if(it.id>billAndGoldIds) billAndGoldIds = it.id }
        billAndGoldIds++; isBillNotFound.value = false
        goldBhav = bill.bhav
        cashReceived = bill.cashReceived
        party.value = Party(name=bill.partyName, phoneNumber = bill.partyNumber)
        calculate()
        loadedBill.value = bill
        isBillSaved.value = true
    }

    fun updateItemsPositions(updatedList: List<Item>) = viewModelScope.launch {
        itemsRepository.updateAll(updatedList)
    }

    fun deleteBill() = viewModelScope.launch {
        isLoading.value = true
        billRepository.deleteBill(loadedBill.value!!)
        updateSales(isDelete = true)
        loadedBill.value = null
        isLoading.value = false
    }

    private fun tab(size:Int) :String {
        val s = StringBuilder()
        for(i in 0..size) s.append(" ")
        return s.toString()
    }

}