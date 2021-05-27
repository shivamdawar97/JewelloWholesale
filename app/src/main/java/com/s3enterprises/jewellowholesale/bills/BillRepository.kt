package com.s3enterprises.jewellowholesale.bills

import com.s3enterprises.jewellowholesale.Utils
import android.text.format.DateFormat
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.s3enterprises.jewellowholesale.Utils.FIRESTORE
import com.s3enterprises.jewellowholesale.database.models.Bill
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.party.PartyRepository
import com.s3enterprises.jewellowholesale.sales.SalesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object BillRepository {

    private val billsCollection by lazy {
        val date = Date()
        val year = DateFormat.format("yyyy", date) as String // 2021
        FIRESTORE.document("bills").collection(year)
    }

    suspend fun insert(bill:Bill) = suspendCoroutine<Bill> { cont ->
        Firebase.firestore.runTransaction { transition ->
            val snap = transition.get(Utils.KEY_VALUES)
            val billNo = snap.get("bill_counter",Int::class.java)!! +1
            transition.update(Utils.KEY_VALUES,"bill_counter",billNo)
            billNo
        }.addOnSuccessListener { billNo ->
            val billRef = billsCollection.document(billNo.toString())
            val saleRef = SalesRepository.currentMonthSale
            val partySaleRef = SalesRepository.partySalesDoc

            Firebase.firestore.runBatch { batch ->
                batch.set(billRef,bill)
                batch.update(saleRef,"gold",FieldValue.increment(bill.goldReceivedFine.toDouble()))
                batch.update(saleRef,"cash",FieldValue.increment(bill.cashReceived.toDouble()))
                batch.update(saleRef,"total",FieldValue.increment(bill.tAmount.toDouble()))
                batch.update(partySaleRef,bill.partyName,FieldValue.increment(bill.tAmount.toDouble()))

            }.addOnSuccessListener {
                bill.billNo = billNo
                cont.resume(bill)
            }.addOnFailureListener {
                    throw it
            }
        }.addOnFailureListener {
            throw it
        }
    }

    suspend fun checkForPartyDoc(partyName:String) = suspendCoroutine<Unit> { cont ->
        val partySaleRef = SalesRepository.partySalesDoc
        partySaleRef.get().addOnSuccessListener {
            if(!it.exists())
                partySaleRef.set(mapOf(partyName to 0f)).addOnSuccessListener {
                    cont.resume(Unit)
                }.addOnFailureListener { ex -> throw ex }
            else if(!it.contains(partyName))
            {
                partySaleRef.update(partyName,0f)
                cont.resume(Unit)
            }
            else cont.resume(Unit)
        }.addOnFailureListener { throw it }
    }

    suspend fun checkForSalesDoc() = suspendCoroutine<Unit> { cont ->
        val saleRef = SalesRepository.currentMonthSale
        saleRef.get().addOnSuccessListener {
            if(!it.exists())
                saleRef.set(mapOf("gold" to 0f,"cash" to 0,"total" to 0)).addOnSuccessListener {
                    cont.resume(Unit)
                }.addOnFailureListener { ex -> throw ex }
            else cont.resume(Unit)
        }.addOnFailureListener { throw it }
    }

    suspend fun getBill(billNo: Int) =  suspendCoroutine<Bill?> { cont ->
        billsCollection.document(billNo.toString()).get().addOnSuccessListener {
            if(it.exists()){
                val bill = it.toObject(Bill::class.java)
                cont.resume(bill)
            } else cont.resume(null)
        }
    }
}