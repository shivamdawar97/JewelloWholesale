package com.s3enterprises.jewellowholesale.billing

import android.util.Log
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

    val expanded = MutableLiveData<Boolean>().apply { value = false }
    var billAndGoldIds = 0
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

    val goldItemList = ArrayList<GoldItem>().apply {
        val goldItem = GoldItem(billAndGoldIds++)
        add(goldItem)
    }
    val grossGR = MutableLiveData<Float>().apply { value = 0f }
    val fineGR = MutableLiveData<Float>().apply { value = 0f }

    val fineBH = MutableLiveData<Float>().apply { value = 0f }
    val fineCR = MutableLiveData<Float>().apply { value = 0f }

    val fineCB = MutableLiveData<Float>().apply { value = 0f }
    val fineFB = MutableLiveData<Float>().apply { value = 0f }

    val cashBH = MutableLiveData<Int>().apply { value = 0 }

    var cashReceived = 0
    var cashBalance = 0; var fineBalance = 0f
    val fineDU = MutableLiveData<Float>().apply { value = 0f }
    val cashDU = MutableLiveData<Int>().apply { value = 0 }
    var goldBhav = 0
    var billCounter = 0


    fun calculate() {
        var grossGs = 0f; var fineGs = 0f ;var grossGr = 0f; var fineGr = 0f
        billItemList.forEach {
            if(it.weight>0) grossGs+=it.weight
            fineGs+=it.fine
        }

        goldItemList.forEach {
            grossGr+=it.weight
            fineGr+=it.fine
        }
        val fineFb = fineGs + fineBalance

        val fineBh = fineFb - fineGr
        val cashBh = (fineBh * goldBhav).toInt()

        val fineCb = if(goldBhav<1) 0f else cashBalance.toFloat() / goldBhav
        val fineCr = if(goldBhav<1) 0f else cashReceived.toFloat() / goldBhav

        val fineDu = (fineBh+fineCb-fineCr).roundOff(3)

        val cashDu = cashBh + cashBalance - cashReceived

        grossGS.value =  grossGs.roundOff(3)
        fineGS.value = fineGs.roundOff(3)

        grossGR.value =  grossGr.roundOff(3)
        fineGR.value = fineGr.roundOff(3)

        cashBH.value = cashBh

        fineBH.value = fineBh.roundOff(3)
        fineCR.value = fineCr.roundOff(3)
        fineCB.value = fineCb.roundOff(3)
        fineFB.value = fineFb.roundOff(3)
        fineDU.value = fineDu
        cashDU.value = cashDu

        isBillSaved.value = false
    }

    fun clearAll() {
        isBillNotFound.value = false
        billItemList.clear(); goldItemList.clear()
        cashReceived = 0 ; party.value = null
        cashBalance = 0; fineBalance = 0f
        billAndGoldIds = 0
        val goldItem = GoldItem(billAndGoldIds++)
        goldItemList.add(goldItem)
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
            loadedBill.value = newBill
            isLoading.value = false
            isBillSaved.value = true
            saveBillString(newBill)
            billRepository.insert(newBill)
            RxBus.publish(RxEvent.PreferencesUpdated())
            updateSales()
        }
        else if(!isBillNotFound.value!!) viewModelScope.launch {
            isLoading.value = true
            val updatedBill = generateBill(loadedBill.value!!)
            loadedBill.value = updatedBill
            isLoading.value = false
            isBillSaved.value = true
            saveBillString(updatedBill)
            billRepository.update(updatedBill)
            updateSales(updatedBill)
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
            .append(tab(18))
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
            .append("${fineGS.value}\n")

        if(fineBalance!=0f) {
            var det2 = 0
            stringBuilder.append("Fine Balance".apply { det2+=length })
                .append(tab(16-det2).apply { det2+=length })
                .append("$fineBalance".apply { det2+=length })
                .append(tab(34-det2))
                .append("${fineFB.value}\n")
        }

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
        var det2 = "BHAV ${it.bhav}".length+5
        val stringBuilder = StringBuilder()
            .append(tab(16-det2).apply { det2+=length })
            .append(Utils.getRupeesFormatted(cashBH.value?:0).apply { det2+=length })
            .append(tab(34-det2+1))
            .append("${fineBH.value ?: 0f}\n")

        if(cashBalance!=0) {
            var det5 = 0
            stringBuilder.append("Cash Balance".apply { det5+=length })
                .append(tab(16-det5).apply { det5+=length })
                .append(Utils.getRupeesFormatted(cashBalance).apply { det5+=length })
                .append(tab(34-det5))
                .append("${fineCB.value ?: 0f}\n")
        }

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
        .append(tab(34-det4-6))
        .append("${fineDU.value ?: 0f}\n\n\n").toString()
    }

    fun generateBillPrint5() =
        StringBuilder().append("Gross Weight:\n\n\n")
        .append("_________________Thank you________________\n\n\n\n").toString()

    private fun generateBill(oldBill: Bill?=null):Bill {
        val bill = oldBill?:Bill(
            billNo = billCounter,
            date = Date().time,
        )
        with(bill){
            partyName = if(party.value==null) "N/A" else party.value!!.name
            partyNumber = if(party.value==null) "N/A" else party.value!!.phoneNumber
            items = Converters().fromList(billItemList)
            golds = Converters().fromList(goldItemList)
            bhav = goldBhav
            cashReceived = this@BillingViewModel.cashReceived
            cashBalance = this@BillingViewModel.cashBalance
            cashDu = cashDU.value!!
            fineGs = fineGS.value!!
            fineBalance = this@BillingViewModel.fineBalance
        }
        return bill
    }

    private fun saveBillString(bill: Bill) = bill.apply{
        billString = generateBillPrint1()+ generateBillPrint2()+
                generateBillPrint3()+ generateBillPrint4()
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
        billAndGoldIds = if(billItemList.isNotEmpty()) billItemList[0].iId else 0
        billItemList.forEach { if(it.iId>billAndGoldIds) billAndGoldIds = it.iId }
        goldItemList.forEach { if(it.id>billAndGoldIds) billAndGoldIds = it.id }
        billAndGoldIds++; isBillNotFound.value = false
        goldBhav = bill.bhav
        cashReceived = bill.cashReceived
        cashBalance = bill.cashBalance
        fineBalance = bill.fineBalance
        party.value = Party(name=bill.partyName, phoneNumber = bill.partyNumber)
        loadedBill.value = bill
        calculate()
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