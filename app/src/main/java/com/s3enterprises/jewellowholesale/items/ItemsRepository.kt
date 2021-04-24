package com.s3enterprises.jewellowholesale.items

import com.s3enterprises.jewellowholesale.Utils.FIRESTORE
import com.s3enterprises.jewellowholesale.database.models.Item
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ItemsRepository {

    private val itemsCollection = FIRESTORE.collection("items")
    private var items:List<Item>? = null

    suspend fun insert(item:Item) = suspendCoroutine<Item> { continuation ->

        itemsCollection.add(item).addOnSuccessListener { doc->
            item.iId = doc.id
            continuation.resume(item)
        }.addOnFailureListener {
            throw it
        }

    }

    suspend fun getItems() = suspendCoroutine<List<Item>> { continuation ->
        if(items==null){
            val newItems = ArrayList<Item>()
            itemsCollection.get().addOnSuccessListener {
                it.documents.forEach { doc ->
                    val item = doc.toObject(Item::class.java)
                    item!!.iId = doc.id
                    newItems.add(item)
                }
                items = newItems
                continuation.resume(items!!)
            }.addOnFailureListener {
                throw it
            }

        }
    }


}