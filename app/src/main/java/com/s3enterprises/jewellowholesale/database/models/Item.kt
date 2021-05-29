package com.s3enterprises.jewellowholesale.database.models

data class Item(
    var iId:String?=null,
    val name:String="NA",
    val rate:Float=0f,
    val stock:Float=0f,
    val position:Int = 0
)