package com.s3enterprises.jewellowholesale.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Bill(
    @PrimaryKey(autoGenerate = true)
    var billNo:Int=0,
    @ColumnInfo
    val date:Long =0,
    @ColumnInfo
    val partyId:String="N/A",
    @ColumnInfo
    val partyName:String="N/A",
    @ColumnInfo
    val partyNumber:String="N/A",
    @ColumnInfo
    var items:String="N/A",
    @ColumnInfo
    var golds:String="N/A",
    @ColumnInfo(name = "gross_gs")
    var grossGs:Float=0f,
    @ColumnInfo(name = "fine_gs")
    var fineGs:Float=0f,
    @ColumnInfo(name = "gross_gr")
    var grossGr:Float=0f,
    @ColumnInfo(name = "fine_gr")
    var fineGr:Float=0f,
    @ColumnInfo
    var bhav:Int=0,
    @ColumnInfo
    var tAmount:Int=0,
    @ColumnInfo
    var cashReceived:Int=0,
    @ColumnInfo
    var dueGold:Float=0f,
    @ColumnInfo
    var dueAmount:Int=0
):Serializable