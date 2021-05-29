package com.s3enterprises.jewellowholesale.database.models

data class Item(
    var iId:String?=null,
    var name:String="NA",
    var rate:Float=0f,
    var position:Int = 0
)