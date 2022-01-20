package com.s3enterprises.jewellowholesale.rx

import com.s3enterprises.jewellowholesale.database.models.Bill
import com.s3enterprises.jewellowholesale.database.models.BillItem
import com.s3enterprises.jewellowholesale.database.models.GoldItem

class RxEvent {
    class EventBillItemChanged
    data class EventBillItemRemoved(val id: Int)
    data class EventGoldItemRemoved(val id: Int)
    class PreferencesUpdated
    class PreviousBillSelected(val bill: Bill)
}