package com.s3enterprises.jewellowholesale.sales

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.Utils
import com.s3enterprises.jewellowholesale.Utils.roundOff
import com.s3enterprises.jewellowholesale.database.models.Sales
import java.util.*

class SalesRecyclerAdapter(private val sales:List<Sales>,private val onClearClicked: (Long)->Unit) :RecyclerView.Adapter<SalesRecyclerAdapter.ViewHolder>(){

    inner class ViewHolder(private val mView: View): RecyclerView.ViewHolder(mView){
        fun populate(sale:Sales){
            val day = DateFormat.format("dd MMM,yyyy", Date(sale.date)) as String
            mView.findViewById<TextView>(R.id.date).text = day
            mView.findViewById<TextView>(R.id.gold).text = sale.gold.roundOff(3).toString()
            mView.findViewById<TextView>(R.id.cash).text = Utils.getRupeesFormatted(sale.cash)
            mView.findViewById<TextView>(R.id.stock).text = sale.stock.roundOff(3).toString()
            mView.findViewById<TextView>(R.id.clear_btn).setOnClickListener {
                onClearClicked(sale.date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_month_wise,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.populate(sales[position])
    }

    override fun getItemCount() = sales.size
}