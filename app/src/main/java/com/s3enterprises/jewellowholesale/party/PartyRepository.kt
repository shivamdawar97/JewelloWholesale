package com.s3enterprises.jewellowholesale.party

import com.s3enterprises.jewellowholesale.database.models.Party
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.s3enterprises.jewellowholesale.Utils.FIRESTORE

object PartyRepository {

    private val partyCollection = FIRESTORE.collection("parties")
    private var parties :List<Party>? = null

    suspend fun insert(party:Party) = suspendCoroutine<String> { continuation ->
        partyCollection.add(party).addOnSuccessListener { doc ->
            continuation.resume(doc.id)
        }.addOnFailureListener {
            throw it
        }
    }

    suspend fun getParties() = suspendCoroutine<List<Party>> { continuation ->
        if(parties==null) {
            val newParties = ArrayList<Party>()
            partyCollection.get().addOnSuccessListener {
                it.documents.forEach { doc ->
                    val party = doc.toObject(Party::class.java)
                    party!!.pId = doc.id
                    newParties.add(party)
                }
                parties = newParties
                continuation.resume(parties!!)
            }.addOnFailureListener {
                throw it
            }
        } else continuation.resume(parties!!)
    }


}