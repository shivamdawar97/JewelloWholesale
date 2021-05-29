package com.s3enterprises.jewellowholesale.items

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.s3enterprises.jewellowholesale.Utils.FIRESTORE
import com.s3enterprises.jewellowholesale.database.models.Item
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.party.PartyRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ItemsRepository {

    private val itemsCollection = FIRESTORE.document("data").collection("items")
    private var _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>>
        get() = _items

    suspend fun insert(item:Item) = suspendCoroutine<Item> { continuation ->

        itemsCollection.add(item).addOnSuccessListener { doc->
            item.iId = doc.id
            continuation.resume(item)
        }.addOnFailureListener {
            throw it
        }

    }

    fun loadItems(hardReload:Boolean=false)  {
        if(_items.value==null || hardReload){
            val newItems = ArrayList<Item>()
            itemsCollection.get().addOnSuccessListener {
                it.documents.forEach { doc ->
                    val item = doc.toObject(Item::class.java)
                    item!!.iId = doc.id
                    newItems.add(item)
                }
                _items.value = newItems
            }.addOnFailureListener {
                throw it
            }
        }
    }

}