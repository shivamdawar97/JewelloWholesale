package com.s3enterprises.jewellowholesale.sales

import com.google.firebase.firestore.DocumentReference
import com.s3enterprises.jewellowholesale.Utils
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object SalesRepository {

    private val salesDoc = Utils.FIRESTORE.document("sales")

    val currentMonthSale by lazy {
        salesDoc.collection(Utils.CurrentDate.year).document(Utils.CurrentDate.month)
    }
    var todaySale:DocumentReference? = null

    val partySalesDoc by lazy {
        salesDoc.collection(Utils.CurrentDate.year).document("parties")
    }

    suspend fun getTodaySaleRef() = suspendCoroutine<Unit>  { cont ->
        if(todaySale==null){
            val date = Utils.CurrentDate.day.toInt()
            todaySale = salesDoc.collection(Utils.CurrentDate.year).document("today")
            todaySale?.get()?.addOnSuccessListener { doc ->
                val docDate = doc["date"].toString().toLong()
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = docDate
                val dd = calendar.get(Calendar.DATE)
                if(dd!=date){
                    salesDoc.collection(Utils.CurrentDate.year).document("today").set(mapOf(
                        "gold" to 0f,
                        "cash" to 0,
                        "total" to 0,
                        "date" to Date().time
                    )).addOnSuccessListener {
                        cont.resume(Unit)
                    }.addOnFailureListener {
                        throw it
                    }
                } else cont.resume(Unit)
            }
        }
        else cont.resume(Unit)
    }

    suspend fun getTodaySale() = suspendCoroutine<Sale> {

    }

}