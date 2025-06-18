package com.s3enterprises.jewellowholesale.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.s3enterprises.jewellowholesale.database.models.Party

@Dao
interface PartyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(party: Party)

    @Update
    suspend fun update(party: Party)

    @Query("select * from party")
    fun getAll(): LiveData<List<Party>>

    @Delete
    suspend fun delete(party: Party)

    @Query("delete from party")
    fun clear()


}