package com.s3enterprises.jewellowholesale.bills


import android.text.format.DateFormat
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.s3enterprises.jewellowholesale.Utils.FIRESTORE
import com.s3enterprises.jewellowholesale.database.dao.BillDao
import com.s3enterprises.jewellowholesale.database.models.Bill
import com.s3enterprises.jewellowholesale.sales.SalesRepository
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BillRepository @Inject constructor (private val billDao: BillDao) {

    suspend fun insert(bill:Bill) = billDao.insert(bill)

    suspend fun update(newBill:Bill) {
        billDao.update(newBill)
    }

    suspend fun getBill(billNo: Int) =  billDao.getBill(billNo)

    suspend fun getBills(dayStart:Long,dayEnd:Long)=
        billDao.getBillsByRange(dayStart,dayEnd)

}