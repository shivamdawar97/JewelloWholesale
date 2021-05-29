package com.s3enterprises.jewellowholesale.rx

import com.s3enterprises.jewellowholesale.database.models.Bill

class RxEvent {
    class EventBillItemChanged
    data class EventBillItemRemoved(val id:String)
    class BhavUpdated
    class PreviousBillSelected(val bill: Bill)
}