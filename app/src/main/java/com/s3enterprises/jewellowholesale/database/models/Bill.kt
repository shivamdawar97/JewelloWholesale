package com.s3enterprises.jewellowholesale.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Bill(
    @PrimaryKey(autoGenerate = false)
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
    val items:String="N/A",
    @ColumnInfo
    val gross:Float=0f,
    @ColumnInfo
    val fine:Float=0f,
    @ColumnInfo
    val bhav:Int=0,
    @ColumnInfo
    var tAmount:Int=0,
    @ColumnInfo
    val goldReceived:Float=0f,
    @ColumnInfo
    val receivedRate:Float=0f,
    @ColumnInfo
    val goldReceivedFine:Float=0f,
    @ColumnInfo
    val cashReceived:Int=0,
    @ColumnInfo
    val dueGold:Float=0f,
    @ColumnInfo
    val dueAmount:Int=0
):Serializable