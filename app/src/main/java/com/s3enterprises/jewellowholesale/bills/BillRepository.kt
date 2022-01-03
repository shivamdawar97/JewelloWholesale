package com.s3enterprises.jewellowholesale.bills

import com.s3enterprises.jewellowholesale.database.dao.BillDao
import com.s3enterprises.jewellowholesale.database.models.Bill
import javax.inject.Inject

class BillRepository @Inject constructor (private val billDao: BillDao) {

    suspend fun insert(bill:Bill) = billDao.insert(bill)

    suspend fun update(newBill:Bill) {
        billDao.update(newBill)
    }

    suspend fun getBill(billNo: Int) =  billDao.getBill(billNo)

    suspend fun getBills(dayStart:Long,dayEnd:Long)=
        billDao.getBillsByRange(dayStart,dayEnd)

}