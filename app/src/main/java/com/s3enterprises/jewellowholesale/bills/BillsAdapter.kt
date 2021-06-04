package com.s3enterprises.jewellowholesale.bills

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.database.models.Bill
import com.s3enterprises.jewellowholesale.rx.RxBus
import com.s3enterprises.jewellowholesale.rx.RxEvent

class BillsAdapter(private val bills:List<Bill>) : RecyclerView.Adapter<BillsAdapter.ViewHolder>()  {
     inner class ViewHolder(private val mView: View): RecyclerView.ViewHolder(mView) {
         private val billNoView = mView.findViewById<TextView>(R.id.bill_no)
         private val amount = mView.findViewById<TextView>(R.id.total_amount)
         private val partyNameView = mView.findViewById<TextView>(R.id.party_name)
         private val dateView = mView.findViewById<TextView>(R.id.date_view)

         fun setData(bill: Bill) = with(bill){

             billNoView.text = billNo.toString()
             amount.text = "₹ $tAmount"
             partyNameView.text = partyName
             dateView.text = Utils.getDate(date)
             mView.setOnClickListener {
                 RxBus.publish(RxEvent.PreviousBillSelected(bill))
                 (mView.context as BillsActivity).finish()
             }
         }

     }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(bills[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_bill_record,parent,false))

    override fun getItemCount() = bills.size
}