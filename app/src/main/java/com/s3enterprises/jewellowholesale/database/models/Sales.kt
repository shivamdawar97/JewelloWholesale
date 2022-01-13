package com.s3enterprises.jewellowholesale.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Sales (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo
    val date:Long=0,

    @ColumnInfo
    var cash:Int=0,

    @ColumnInfo
    var gold:Float=0f,

    @ColumnInfo
    var total:Int=0

)
