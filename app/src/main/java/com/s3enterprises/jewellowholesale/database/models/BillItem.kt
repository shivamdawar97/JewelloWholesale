package com.s3enterprises.jewellowholesale.database.models

data class BillItem(
    var iId:String?=null,
    val name:String="NA",
    val weight:Float=0f,
    val rate:Float=0f,
    val fine:Float=-0f
)