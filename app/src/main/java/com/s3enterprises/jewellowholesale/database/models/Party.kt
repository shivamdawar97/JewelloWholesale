package com.s3enterprises.jewellowholesale.database.models

data class Party(
    var pId:String?=null,
    val name:String,
    val phoneNumber:String,
    val from:String,
    val lastArrival:String="NA"
)
