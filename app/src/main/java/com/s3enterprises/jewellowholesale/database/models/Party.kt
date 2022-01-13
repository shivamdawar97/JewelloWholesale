package com.s3enterprises.jewellowholesale.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(indices =[Index(value = ["name"], unique = true)])
data class Party(
    @PrimaryKey(autoGenerate = true)
    var pId:Int=0,
    @ColumnInfo
    var name:String="NA",
    @ColumnInfo(name = "phone_number")
    var phoneNumber:String="NA"
):Serializable
