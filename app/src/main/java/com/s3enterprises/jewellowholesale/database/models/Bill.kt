package com.s3enterprises.jewellowholesale.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Bill(
    @PrimaryKey(autoGenerate = false)
    var billNo:Int=0,
    @ColumnInfo
    val date:Long =0,
    @ColumnInfo
    var partyName:String="N/A",
    @ColumnInfo
    var partyNumber:String="N/A",
    @ColumnInfo
    var items:String="N/A",
    @ColumnInfo
    var golds:String="N/A",
    @ColumnInfo
    var bhav:Int=0,
    @ColumnInfo
    var cashReceived:Int=0,
    @ColumnInfo
    var cashBalance:Int=0,
    @ColumnInfo(name = "cash_du")
    var cashDu:Int=0,
    @ColumnInfo(name = "fine_gs")
    var fineGs:Float=0f,
    @ColumnInfo(name = "fine_fb")
    var fineBalance:Float=0f,
    @ColumnInfo
    var billString: String = "",
    @ColumnInfo
    var totalStockSold: Float = 0f,
    @ColumnInfo
    var totalGoldReceived:Float = 0f
):Serializable