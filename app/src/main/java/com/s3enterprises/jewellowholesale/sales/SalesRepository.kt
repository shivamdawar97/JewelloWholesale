package com.s3enterprises.jewellowholesale.sales

import com.google.firebase.firestore.DocumentReference
import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.Utils.roundOff
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object SalesRepository {

    private val salesDoc = Utils.FIRESTORE.document("sales")
    private val currentYearSale by lazy { salesDoc.collection(Utils.CurrentDate.year) }
    val currentMonthSale by lazy { currentYearSale.document(Utils.CurrentDate.month) }
    var todaySaleRef:DocumentReference? = null
    val partySalesDoc by lazy {
        salesDoc.collection(Utils.CurrentDate.year).document("parties")
    }

    suspend fun getTodaySaleRef() = suspendCoroutine<Unit>  { cont ->
        if(todaySaleRef==null){
            val date = Utils.CurrentDate.day.toInt()
            todaySaleRef = salesDoc.collection(Utils.CurrentDate.year).document("today")
            todaySaleRef?.get()?.addOnSuccessListener { doc ->
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

    suspend fun getCurrentYearSale() = suspendCoroutine<List<Sale>> { cont ->
        currentYearSale.get().addOnSuccessListener { snap ->
            val list = ArrayList<Sale>()
            snap.documents.forEach { doc ->
                if(doc.id != "parties") {
                    val gold = doc["gold"].toString().toFloat().roundOff(3).toString()
                    val cash = doc["cash"].toString().toFloat().toInt().toString()
                    val total = doc["total"].toString().toFloat().toInt().toString()
                    val sale = Sale(doc.id,gold,cash,total)
                    list.add(sale)
                }else doc.data?.forEach { entry ->
                    val total = entry.value.toString().toFloat().toInt().toString()
                    val sale = Sale("party",entry.key,"0",total)
                    list.add(sale)
                }
            }
            cont.resume(list)
        }.addOnFailureListener { throw it }
    }


}