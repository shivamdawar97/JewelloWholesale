package com.s3enterprises.jewellowholesale.database.dao


import androidx.room.*
import com.s3enterprises.jewellowholesale.database.models.Bill

@Dao
interface BillDao {

    @Insert
    suspend fun insert(bill: Bill):Long

    @Update
    suspend fun update(bill: Bill)

    @Query("select * from Bill")
    fun getAll(): List<Bill>

    @Delete
    fun delete(item: Bill)

    @Query("delete from Bill")
    fun clear()



}