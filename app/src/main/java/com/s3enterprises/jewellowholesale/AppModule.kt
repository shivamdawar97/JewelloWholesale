package com.s3enterprises.jewellowholesale

import android.content.Context
import com.s3enterprises.jewellowholesale.bills.BillRepository
import com.s3enterprises.jewellowholesale.database.JewelloDatabase
import com.s3enterprises.jewellowholesale.database.dao.BillDao
import com.s3enterprises.jewellowholesale.database.dao.ItemDao
import com.s3enterprises.jewellowholesale.items.ItemsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Scope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext app: Context) = JewelloDatabase.getInstance(app)

    @Singleton
    @Provides
    fun getSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences("jewello_wholesale", Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideBillDao(database: JewelloDatabase) = database.billDao

    @Singleton
    @Provides
    fun provideItemDao(database: JewelloDatabase) = database.itemDao

    @Singleton
    @Provides
    fun providePartyDao(database: JewelloDatabase) = database.partyDao

    @Singleton
    @Provides
    fun provideSalesDao(database: JewelloDatabase) = database.salesDao

}