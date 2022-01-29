package com.s3enterprises.jewellowholesale.database.dao


import androidx.room.*
import com.s3enterprises.jewellowholesale.database.models.Bill
import kotlinx.coroutines.selects.select

@Dao
interface BillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bill: Bill):Long

    @Update
    suspend fun update(bill: Bill)

    @Query("select * from Bill where date between (:dayStart) and :dayEnd")
    suspend fun getBillsByRange(dayStart:Long,dayEnd:Long): List<Bill>

    @Delete
    suspend fun delete(item: Bill)

    @Query("delete from Bill")
    fun clear()

    @Query("select  * from Bill where billNo = :billNo")
    suspend fun getBill(billNo: Int):Bill?

    @Query("select  * from Bill where partyName = :pName")
    suspend fun getBillsByPartyName(pName: String): List<Bill>

}