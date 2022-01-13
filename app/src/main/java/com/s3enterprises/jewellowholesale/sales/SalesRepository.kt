package com.s3enterprises.jewellowholesale.sales

import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.database.dao.SalesDao
import com.s3enterprises.jewellowholesale.database.models.Sales
import java.util.*
import javax.inject.Inject

class SalesRepository @Inject constructor(private val salesDao: SalesDao) {

    suspend fun updateTodaySale(cash:Int,gold:Float,total:Int){
        val atDayStart = Utils.atStartOfDay(Date()).time
        val todaySale = getSale(atDayStart)?: Sales(atDayStart)
        todaySale.cash = todaySale.cash + cash
        todaySale.gold = todaySale.gold + gold
        todaySale.total = todaySale.total + total
        return insert(todaySale)
    }

    suspend fun insert(sales:Sales) = salesDao.insert(sales)

    suspend fun update(newSales:Sales)=salesDao.update(newSales)

    suspend fun getSale(date:Long) = salesDao.getSale(date)

    suspend fun getAll() = salesDao.getAll()

    suspend fun getSales(monthStart:Long,monthEnd:Long) =
        salesDao.getSalesByRange(monthStart,monthEnd)

    suspend fun deleteSale(sales: Sales) = salesDao.delete(sales)
}