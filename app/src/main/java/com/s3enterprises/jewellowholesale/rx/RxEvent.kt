package com.s3enterprises.jewellowholesale.rx

import com.s3enterprises.jewellowholesale.database.models.Bill
import com.s3enterprises.jewellowholesale.database.models.BillItem
import com.s3enterprises.jewellowholesale.database.models.GoldItem

class RxEvent {
    class EventBillItemChanged
    data class EventBillItemRemoved(val item:BillItem)
    data class EventGoldItemRemoved(val item:GoldItem)
    class PreferencesUpdated
    class PreviousBillSelected(val bill: Bill)
    class PendingBillSelected(val pending: Bill)
}