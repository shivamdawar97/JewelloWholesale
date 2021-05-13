package com.s3enterprises.jewellowholesale.billing

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s3enterprises.jewellowholesale.Utils.bhav
import com.s3enterprises.jewellowholesale.Utils.roundOff
import com.s3enterprises.jewellowholesale.Utils.stringToFloat
import com.s3enterprises.jewellowholesale.database.models.BillItem
import com.s3enterprises.jewellowholesale.database.models.Item
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.items.ItemsRepository
import kotlinx.coroutines.launch

class BillingViewModel:ViewModel() {

    val items:List<Item>
    get() = ItemsRepository.getSavedItems()!!
    val billItemList = ArrayList<BillItem>()
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

        val goldOfCashRvd = cashRvd / bhav
        val totalGoldRvd = rcdFine + goldOfCashRvd
        val dueGold = fine - totalGoldRvd
        val dueAmount = (dueGold * bhav).toInt()

        grossWeight.value =  gross.toString()
        fineWeight.value = fine.toString()
        totalAmount.value = tAmount.toString()
        goldFine.value = rcdFine.roundOff(3).toString()
        dueFineGold.value = dueGold.roundOff(3).toString()
        dueCash.value = dueAmount.toString()

    }

    fun clearAll() {
        billItemList.clear()
        calculate()
        goldPurity.value = 99.50.toString()
    }

}