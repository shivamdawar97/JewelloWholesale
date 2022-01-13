package com.s3enterprises.jewellowholesale.sales

import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s3enterprises.jewellowholesale.bills.BillRepository
import com.s3enterprises.jewellowholesale.database.models.Sales
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject  constructor(private val salesRepository: SalesRepository):ViewModel() {

    val saleRange = MutableLiveData<String>()
    var month:Int=0; var year:Int=0
    val isLoading = MutableLiveData<Boolean>().apply { value = false }
    val date = MutableLiveData<Date>().apply { value = Date() }
    val sales = MediatorLiveData<List<Sales>>().apply {
        addSource(date){
            viewModelScope.launch {
                isLoading.value = true
                calculateMonthStartAndEndTime()
                isLoading.value = false

            }
        }
    }


    private suspend fun calculateMonthStartAndEndTime() {

        var mycal: Calendar = GregorianCalendar(year, month, 1)
        val startDate = mycal.timeInMillis

        val lastDate = mycal.getActualMaximum(Calendar.DAY_OF_MONTH)
        mycal = GregorianCalendar(year, month, lastDate)
        val endDate = mycal.getTimeInMillis()

        sales.value = salesRepository.getSales(startDate,endDate)
        val day1 = DateFormat.format("dd MMM,yyyy", Date(startDate)) as String
        val day2 = DateFormat.format("dd MMM,yyyy", Date(endDate)) as String
        saleRange.value = "Sales from $day1 to $day2"
    }
}