package com.s3enterprises.jewellowholesale.database.models

import java.io.Serializable

data class Party(
    var pId:Int?=0,
    var name:String="NA",
    var phoneNumber:String="NA",
    var from:String="NA",
    var lastArrival:String="NA"
):Serializable
