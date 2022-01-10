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
    fun getAll(): List<Sales>

    @Delete
    fun delete(sales: Sales)

    @Query("delete from Sales")
    fun clear()


}