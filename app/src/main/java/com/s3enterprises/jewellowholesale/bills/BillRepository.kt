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

    private val billsCollection by lazy {
        val date = Date()
        val year = DateFormat.format("yyyy", date) as String // 2021
        FIRESTORE.document("bills").collection(year)
    }

    suspend fun insert(bill:Bill) = billDao.insert(bill)


    suspend fun update(newBill:Bill) {
        billDao.update(newBill)
    }

 /*   suspend fun getBill(billNo: Int) =  suspendCoroutine<Bill?> { cont ->
        billsCollection.document(billNo.toString()).get().addOnSuccessListener {
            if(it.exists()){
                val bill = it.toObject(Bill::class.java)!!
                bill.tAmount = it["tamount"].toString().toInt()
                cont.resume(bill)
            } else cont.resume(null)
        }.addOnFailureListener { throw it }
    }*/

    suspend fun getBills(dayStart:Long,dayEnd:Long) = suspendCoroutine<List<Bill>>{ cont ->
        billsCollection.whereGreaterThan("date",dayStart).whereLessThan("date",dayEnd)
            .get().addOnSuccessListener {
            val list = ArrayList<Bill>()
            it.documents.forEach { doc ->
                val bill = doc.toObject(Bill::class.java)!!
                bill.billNo = doc.id.toInt()
                //bill.tAmount = doc["tamount"].toString().toInt()
                list.add(bill)
            }
            cont.resume(list)
        }.addOnFailureListener {
            throw it
        }
    }
}