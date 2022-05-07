package com.s3enterprises.jewellowholesale.database.dao

import androidx.room.*
import com.s3enterprises.jewellowholesale.database.models.Sales

@Dao
interface SalesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sales: Sales)

    @Update
    suspend fun update(sales: Sales)

    @Query("select * from Sales")
    suspend fun getAll(): List<Sales>

    @Query("delete from Sales where date=:date")
    suspend fun delete(date: Long)

    @Query("delete from Sales")
    fun clear()

    @Query("select * from Sales where date=:date")
    suspend fun getSale(date:Long):Sales?

    @Query("select * from Sales where date between (:monthStart) and :monthEnd order by date desc")
    suspend fun getSalesByRange(monthStart: Long, monthEnd: Long): List<Sales>

}