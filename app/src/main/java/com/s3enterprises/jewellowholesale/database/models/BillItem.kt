package com.s3enterprises.jewellowholesale.database.models

data class BillItem(
    var iId:String?=null,
    val name:String="NA",
    var weight:Float=0f,
    var rate:Float=0f,
    var fine:Float=0f
)