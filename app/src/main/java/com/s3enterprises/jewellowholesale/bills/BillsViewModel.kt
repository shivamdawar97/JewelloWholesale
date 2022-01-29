package com.s3enterprises.jewellowholesale.bills

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s3enterprises.jewellowholesale.Utils.atEndOfDay
import com.s3enterprises.jewellowholesale.Utils.atStartOfDay
import com.s3enterprises.jewellowholesale.database.models.Bill
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BillsViewModel @Inject  constructor(private val billRepository: BillRepository):ViewModel()  {

    val isLoading = MutableLiveData<Boolean>().apply { value = false }
    val date = MutableLiveData<Date>()
    val bills = MediatorLiveData<List<Bill>>().apply {
        addSource(date) {
            viewModelScope.launch {
                isLoading.value = true
                value = billRepository.getBills(atStartOfDay(it).time,atEndOfDay(it).time)
                isLoading.value = false
            }
        }
    }

    fun goToPreviousDate() = with(Calendar.getInstance()) {
        time = date.value!!;add(Calendar.DATE, -1)
        date.value = time
    }

    fun goToNextDate() = with(Calendar.getInstance()) {
        time = date.value!!;add(Calendar.DATE, +1)
        date.value = time
    }

    fun getBillsByPId(pName: String) = viewModelScope.launch {
        bills.value = billRepository.getBillsByPartyName(pName)
    }

}