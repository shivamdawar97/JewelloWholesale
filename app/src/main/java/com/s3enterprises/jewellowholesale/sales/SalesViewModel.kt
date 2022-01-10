package com.s3enterprises.jewellowholesale.sales

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.s3enterprises.jewellowholesale.bills.BillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject  constructor(private val salesRepository: SalesRepository):ViewModel() {

    val date = MutableLiveData<Date>().apply { value = Date() }
    
}