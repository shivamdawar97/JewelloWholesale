package com.s3enterprises.jewellowholesale.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.s3enterprises.jewellowholesale.database.models.Bill
import com.s3enterprises.jewellowholesale.database.models.BillItem
import com.s3enterprises.jewellowholesale.database.models.GoldItem

class Converters {

    @TypeConverter
    fun fromString(value: String?): List<BillItem>? {
        val listType = object : TypeToken<List<BillItem>>() {}.type
        return Gson().fromJson(value,listType)
    }

    @TypeConverter
    fun fromString1(value: String?): List<GoldItem>? {
        val listType = object : TypeToken<List<GoldItem>>() {}.type
        return Gson().fromJson(value,listType)
    }



    @TypeConverter
    fun fromList(list: List<Any>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromStringToBills(value: String?): ArrayList<Bill>? {
        val listType = object : TypeToken<ArrayList<Bill>>() {}.type
        return Gson().fromJson(value,listType)
    }

    @TypeConverter
    fun fromListToString(list: ArrayList<Bill>?): String {
        return Gson().toJson(list)
    }


}