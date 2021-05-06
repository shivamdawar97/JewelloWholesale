package com.s3enterprises.jewellowholesale.database.models

data class Bill(
    var bilNo:Int=0,
    val date:Long =0,
    val customerName:String="N/A",
    val phoneNumber:String="N/A",
    val items:String="N/A",
    val gross:Float=0f,
    val fine:Float=0f,
    val tAmount:Int=0,
    val goldReceived:Float=0f,
    val receivedRate:Float=0f,
    val goldReceivedFine:Float=0f,
    val cashReceived:Int=0,
    val dueGold:Float=0f,
    val dueAmount:Int=0
)