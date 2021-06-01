package com.s3enterprises.jewellowholesale.database.models

import java.io.Serializable

data class Item(
    var iId:String?=null,
    var name:String="NA",
    var rate:Float=0f,
    var position:Int = 0
):Serializable