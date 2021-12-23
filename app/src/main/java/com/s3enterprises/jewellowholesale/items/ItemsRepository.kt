package com.s3enterprises.jewellowholesale.items

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.s3enterprises.jewellowholesale.Utils.FIRESTORE
import com.s3enterprises.jewellowholesale.database.dao.ItemDao
import com.s3enterprises.jewellowholesale.database.models.Item
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.party.PartyRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ItemsRepository @Inject constructor(private val itemDao: ItemDao) {

    var items : LiveData<List<Item>> = itemDao.getAll()

    suspend fun insert(item:Item)  {
        itemDao.insert(item)
    }

    suspend fun update(item:Item) {
      itemDao.update(item)
    }

    suspend fun delete(item:Item){
        itemDao.delete(item)
    }

    suspend fun getCount() = itemDao.getCount()

}