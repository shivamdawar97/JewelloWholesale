package com.s3enterprises.jewellowholesale.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sales (
    @PrimaryKey(autoGenerate = true)
    val id:Long,

    @ColumnInfo
    val date:Long,

    @ColumnInfo
    val cash:Int,

    @ColumnInfo
    val gold:Long

)
