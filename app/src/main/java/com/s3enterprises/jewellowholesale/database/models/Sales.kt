package com.s3enterprises.jewellowholesale.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sales (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo
    val date:Long,

    @ColumnInfo
    val cash:Int,

    @ColumnInfo
    val gold:Float,

    @ColumnInfo
    val total:Float

)
