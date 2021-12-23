package com.s3enterprises.jewellowholesale.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.s3enterprises.jewellowholesale.database.models.Item

@Dao
interface ItemDao {

    @Insert
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Query("select * from Item order by position")
    fun getAll(): LiveData<List<Item>>

    @Delete
    suspend fun delete(item: Item)

    @Query("delete from Item")
    fun clear()

    @Query("select count(*) from Item")
    suspend fun getCount():Int

}