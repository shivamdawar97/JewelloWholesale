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
import com.s3enterprises.jewellowholesale.database.models.*
import com.s3enterprises.jewellowholesale.items.ItemsRepository
import com.s3enterprises.jewellowholesale.party.PartyRepository
import com.s3enterprises.jewellowholesale.sales.SalesRepository
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

    val isloading = MutableLiveData<Boolean>().apply { value = false }
    val isBillLoading = MutableLiveData<Boolean>().apply { value = false }
    val isBillNotFound = MutableLiveData<Boolean>().apply { value = false }

    val billItemList = ArrayList<BillItem>()
    val grossGS = MutableLiveData<String>().apply { value = "0.0" }
    val fineGS = MutableLiveData<String>().apply { value = "0.0" }

    val goldItemList = ArrayList<GoldItem>()
    val grossGR = MutableLiveData<String>().apply { value = "0.0" }
    val fineGR = MutableLiveData<String>().apply { value = "0.0" }


    val totalAmount = MutableLiveData<String>().apply { value = "0" }


    var cashReceived = 0
    val dueFineGold = MutableLiveData<String>().apply { value = "0.0" }
    val dueCash = MutableLiveData<String>().apply { value = "0" }
    var goldBhav = 0

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

        val tAmount = (fineGs * goldBhav).toInt()

        val goldOfCashRvd = if(goldBhav<1) 0f else cashReceived.toFloat() / goldBhav
        val totalGoldRvd = fineGr + goldOfCashRvd
        val dueGold = (fineGs - totalGoldRvd).roundOff(3)

        val totalCashRvd = cashReceived + (fineGr*goldBhav).toInt()
        val dueAmount = tAmount - totalCashRvd

        grossGS.value =  grossGs.roundOff(3).toString()
        fineGS.value = fineGs.roundOff(3).toString()
        totalAmount.value = tAmount.toString()

        dueFineGold.value = dueGold.toString()
        dueCash.value = dueAmount.toString()

    }

    fun clearAll() {
        billNo.value = 0; isBillNotFound.value = false
        billItemList.clear(); goldItemList.clear()
        cashReceived = 0;
        calculate()
    }

    fun findParty(typedName: String) {
        party.value = parties.value?.find { it.name == typedName }
    }

    fun saveBill() { // Or update bill
        calculate()
        if(billNo.value == 0) viewModelScope.launch {
            isloading.value = true
            val newBill = generateBill()
            billNo.value = billRepository.insert(newBill).toInt()
            loadedBill.value = newBill
            isloading.value = false

        }
        else if(!isBillNotFound.value!!) viewModelScope.launch {
            isloading.value = true
            SalesRepository.getTodaySaleRef()
            val updatedBill = generateBill(loadedBill.value!!)
            billRepository.update(updatedBill)
            loadedBill.value = updatedBill
            isloading.value = false
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
                .append("GS \t${it.grossGs}\t\t\t ${it.fineGs}")

        if(fineGR.value!!.toFloat()!=0f)
            goldItemList.forEach { i ->
                stringBuilder.append("${i.weight} * ${i.purity} = ${i.fine}")
            }
        stringBuilder.append("\n----------------------------\n")
            .append("GR \t${it.grossGr}\t\t\t ${it.fineGr}")
            .append("CR ${it.bhav}")
            .append("Total Amount ${it.tAmount}")
        if(it.cashReceived!=0)
            stringBuilder.append("Amount received: ${it.cashReceived}")
        stringBuilder.append("Due Gold: ${it.dueGold}")
        stringBuilder.append("Due Amount: ${it.dueAmount}").toString()
    }


    fun generateBill(oldBill: Bill?=null):Bill {
        val bill = oldBill?:Bill(
            date = Date().time,
            partyId = if(party.value==null) "N/A" else party.value!!.pId.toString(),
            partyName = if(party.value==null) "N/A" else party.value!!.name,
            partyNumber = if(party.value==null) "N/A" else party.value!!.phoneNumber ,
        )
        with(bill){
            items = Converters().fromList(billItemList)
            golds = Converters().fromList(goldItemList)
            grossGs =  stringToFloat(grossGS.value!!)
            fineGs = stringToFloat(fineGS.value!!)
            grossGr =  stringToFloat(grossGR.value!!)
            fineGr = stringToFloat(fineGR.value!!)
            bhav = goldBhav
            tAmount = stringToInt(totalAmount.value!!)
            cashReceived = this@BillingViewModel.cashReceived
            dueGold = stringToFloat(dueFineGold.value!!)
            dueAmount = stringToInt(dueCash.value!!)
        }
        return bill
    }

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
//        TODO if(loadedBill.value == null || billNo.value!! != loadedBill.value!!.billNo)
//            loadedBill.value = BillRepository.getBill(billNo.value!!)
        if(loadedBill.value!=null)
        setUpBill()
        else isBillNotFound.value = true
        isBillLoading.value = false
    }

    private fun setUpBill() = loadedBill.value?.let {
        billItemList.clear()
        billItemList.addAll(Converters().fromString(it.items)!!)
        isBillNotFound.value = false
        findParty(it.partyName)

        grossGS.value = it.grossGs.toString()
        fineGS.value = it.fineGs.toString()

        grossGR.value = it.grossGr.toString()
        fineGR.value = it.fineGr.toString()

        goldBhav = it.bhav
        totalAmount.value = it.tAmount.toString()
        cashReceived = it.cashReceived
        dueFineGold.value = it.dueGold.toString()
        dueCash.value = it.dueAmount.toString()
    }

    fun onOldBillSelected(bill: Bill) {
        loadedBill.value = bill ; billNo.value = bill.billNo
        setUpBill()
    }

    fun updateItemsPositions(updatedList: List<Item>) = viewModelScope.launch {
        itemsRepository.updateAll(updatedList)
    }

}