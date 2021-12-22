package com.s3enterprises.jewellowholesale.rx

import com.s3enterprises.jewellowholesale.database.models.Bill

class RxEvent {
    class EventBillItemChanged
    data class EventBillItemRemoved(val id:Int)
    class BhavUpdated
    class PreviousBillSelected(val bill: Bill)
    class PendingBillSelected(val pending: Bill)
}