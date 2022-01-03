package com.s3enterprises.jewellowholesale.items

import androidx.lifecycle.LiveData
import com.s3enterprises.jewellowholesale.database.dao.ItemDao
import com.s3enterprises.jewellowholesale.database.models.Item
import javax.inject.Inject

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

    suspend fun updateAll(list: List<Item>){
        itemDao.updateAll(*list.toTypedArray())
    }

    suspend fun getCount() = itemDao.getCount()

}