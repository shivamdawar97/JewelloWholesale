package com.s3enterprises.jewellowholesale.database.dao

import androidx.room.*
import com.s3enterprises.jewellowholesale.database.models.Item

@Dao
interface SalesDao {

    @Insert
    suspend fun insert(bill: Item)

    @Update
    suspend fun update(bill: Item)

    @Query("select * from Item")
    fun getAll(): List<Item>

    @Delete
    fun delete(item: Item)

    @Query("delete from Item")
    fun clear()


}