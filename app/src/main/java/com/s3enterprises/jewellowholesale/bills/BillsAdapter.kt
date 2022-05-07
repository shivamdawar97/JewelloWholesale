package com.s3enterprises.jewellowholesale.bills

import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.billing.BillingActivity
import com.s3enterprises.jewellowholesale.database.models.Bill
import com.s3enterprises.jewellowholesale.print.JewelloBluetoothSocket
import com.s3enterprises.jewellowholesale.rx.RxBus
import com.s3enterprises.jewellowholesale.rx.RxEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class BillsAdapter(private val bills:List<Bill>) : RecyclerView.Adapter<BillsAdapter.ViewHolder>()  {


     inner class ViewHolder(private val mView: View): RecyclerView.ViewHolder(mView) {
         private val billNoView = mView.findViewById<TextView>(R.id.bill_no)
         private val amount = mView.findViewById<TextView>(R.id.total_amount)
         private val partyNameView = mView.findViewById<TextView>(R.id.party_name)
         private val dateView = mView.findViewById<TextView>(R.id.date_view)
         private lateinit var previewDialog: Dialog

         fun setData(bill: Bill) = with(bill){

             billNoView.text = billNo.toString()
             amount.text = "₹ $fineGs"
             partyNameView.text = partyName
             dateView.text = Utils.getDate(date)
             mView.setOnClickListener {
                 previewDialog = Dialog(mView.context)
                 previewDialog.setContentView(R.layout.bill_preview_dailog)
                 previewDialog.findViewById<TextView>(R.id.bill_preview).text = bill.billString
                 previewDialog.findViewById<Button>(R.id.print_preview).setOnClickListener {
                     runBlocking {
                         val socket = JewelloBluetoothSocket()
                         socket.findDeviceAndConnect(mView.context)
                         socket.printData(bill.billString)
                         socket.disconnectBT()
                     }
                 }
                 previewDialog.findViewById<Button>(R.id.edit_bill).setOnClickListener {
                     val intent = Intent(mView.context,BillingActivity::class.java)
                     intent.putExtra("bill",bill)
                     intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                     mView.context.startActivity(intent)
                     RxBus.publish(RxEvent.PreviousBillSelected(bill))
                     (mView.context as BillsActivity).finish()
                 }
                previewDialog.show()
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