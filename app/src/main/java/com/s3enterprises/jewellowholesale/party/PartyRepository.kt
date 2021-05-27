package com.s3enterprises.jewellowholesale.party

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.s3enterprises.jewellowholesale.database.models.Party
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.s3enterprises.jewellowholesale.Utils.FIRESTORE
import java.lang.Exception

object PartyRepository {

    private val partyCollection = FIRESTORE.document("data").collection("parties")
    private val _parties = MutableLiveData<List<Party>>()
    val parties:LiveData<List<Party>>
    get() = _parties

    suspend fun insert(party:Party) = suspendCoroutine<Void?> { continuation ->
        val doc = partyCollection.document(party.name)
        val map = mapOf(
                "from" to party.from,
                "lastArrival" to party.lastArrival,
                "phoneNumber" to party.phoneNumber
        )
        doc.get().addOnSuccessListener { snap ->
            if(snap.exists()) throw Exception("Name already Exists")
            else doc.set(map)
                .addOnSuccessListener {
                    val value = _parties.value ?: emptyList()
                    _parties.value = value + listOf(party)
                    continuation.resume(null)
                }
                .addOnFailureListener { throw it }
        }
    }

    fun loadParties()  {
        if(_parties.value==null) {
            val newParties = ArrayList<Party>()
            partyCollection.get().addOnSuccessListener {
                it.documents.forEachIndexed { index, doc ->
                    val party = doc.toObject(Party::class.java)
                    party!!.name = doc.id
                    party.pId = index
                    newParties.add(party)
                }
                _parties.value = newParties
            }.addOnFailureListener {
                throw it
            }
        }
    }

    fun getSavedParties() = _parties.value

    suspend fun update(party: Party) = suspendCoroutine<Void?> { continuation ->
        val doc = partyCollection.document(party.name)
        val map = mapOf(
            "from" to party.from,
            "lastArrival" to party.lastArrival,
            "phoneNumber" to party.phoneNumber
        )
        doc.set(map)
            .addOnSuccessListener { continuation.resume(null) }
            .addOnFailureListener { throw it }
    }

    suspend fun delete(party: Party) = suspendCoroutine<Void?>{ continuation ->
        val doc = partyCollection.document(party.name)
        doc.delete()
            .addOnSuccessListener {
                _parties.value = _parties.value?.filter { p->
                    p.name != party.name
                } as ArrayList<Party>?
                continuation.resume(null)
            }
            .addOnFailureListener { throw it }
    }


}