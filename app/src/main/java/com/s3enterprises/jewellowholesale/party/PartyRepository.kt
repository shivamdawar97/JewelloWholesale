package com.s3enterprises.jewellowholesale.party

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.s3enterprises.jewellowholesale.database.models.Party
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object PartyRepository {

    private val partyCollection = Firebase.firestore.collection("parties")
    private var parties :List<Party>? = null

    suspend fun insert(party:Party) = suspendCoroutine<String> { continuation ->
        val partyDoc = hashMapOf(
            "name" to party.name,
            "phoneNumber" to party.phoneNumber,
            "from" to party.from,
            "lastArrival" to "NA"
        )
        partyCollection.add(partyDoc).addOnSuccessListener {
            continuation.resume(it.id)
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
//                    val party = Party(doc.id,
//                        doc["name"].toString(),
//                        doc["phoneNumber"].toString(),
//                        doc["from"].toString(),
//                        doc["lastArrival"].toString())
                    newParties.add(party)
                }
                parties = newParties
                continuation.resume(parties!!)
            }.addOnFailureListener {
                throw it
            }
        }
    }


}