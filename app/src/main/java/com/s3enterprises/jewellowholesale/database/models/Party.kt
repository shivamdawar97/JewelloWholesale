package com.s3enterprises.jewellowholesale.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Party(
    @PrimaryKey(autoGenerate = true)
    var pId:Int=0,
    @ColumnInfo
    var name:String="NA",
    @ColumnInfo
    var phoneNumber:String="NA",
    @ColumnInfo
    var from:String="NA",
    @ColumnInfo
    var lastArrival:String="NA"
):Serializable
