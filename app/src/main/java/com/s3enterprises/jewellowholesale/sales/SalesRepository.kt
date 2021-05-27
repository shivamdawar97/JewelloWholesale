package com.s3enterprises.jewellowholesale.sales

import com.s3enterprises.jewellowholesale.Utils

object SalesRepository {

    val currentMonthSale by lazy {
        Utils.FIRESTORE.document("sales").collection(Utils.CurrentDate.year).document(Utils.CurrentDate.month)
    }

    val partySalesDoc by lazy {
        Utils.FIRESTORE.document("sales").collection(Utils.CurrentDate.year).document("parties")
    }





}