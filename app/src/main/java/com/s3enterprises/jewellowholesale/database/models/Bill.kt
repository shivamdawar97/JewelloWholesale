package com.s3enterprises.jewellowholesale.database.models

data class Bill(
    var bilNo:Int=0,
    val date:Long =0,
    val partyId:String="N/A",
    val partyName:String="N/A",
    val partyNumber:String="N/A",
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