package com.s3enterprises.jewellowholesale.database

import android.content.Context
import androidx.room.*
import com.s3enterprises.jewellowholesale.database.dao.BillDao
import com.s3enterprises.jewellowholesale.database.dao.ItemDao
import com.s3enterprises.jewellowholesale.database.dao.PartyDao
import com.s3enterprises.jewellowholesale.database.dao.SalesDao
import com.s3enterprises.jewellowholesale.database.models.Bill
import com.s3enterprises.jewellowholesale.database.models.Item
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.database.models.Sales

@Database(entities = [Bill::class,Item::class, Party::class,Sales::class],version = 2,exportSchema = false)
abstract class JewelloDatabase:RoomDatabase() {

    abstract val itemDao:ItemDao
    abstract val billDao:BillDao
    abstract val partyDao:PartyDao
    abstract val salesDao:SalesDao

    companion object {

        @Volatile
        private var INSTANCE:JewelloDatabase? = null

        fun getInstance(context: Context) : JewelloDatabase = synchronized(this) {
                return@synchronized INSTANCE ?: Room.databaseBuilder(
                        context.applicationContext,
                        JewelloDatabase::class.java,
                        "jewello_wholesale_db"
                    ).fallbackToDestructiveMigration().build().also {
                        INSTANCE = it
                }
        }


    }


}