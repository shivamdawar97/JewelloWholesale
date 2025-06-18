package com.s3enterprises.jewellowholesale.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    val iId:Int=0,
    @ColumnInfo
    var name:String="NA",
    @ColumnInfo
    var rate:Float=0f,
    @ColumnInfo
    var position:Int = 0,
    @ColumnInfo
    var isStone: Boolean = false
):Serializable