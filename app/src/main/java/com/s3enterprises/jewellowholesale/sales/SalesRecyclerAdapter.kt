package com.s3enterprises.jewellowholesale.sales

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.database.models.Sales
import java.util.*

class SalesRecyclerAdapter(private val sales:List<Sales>) :RecyclerView.Adapter<SalesRecyclerAdapter.ViewHolder>(){

    class ViewHolder(private val mView: View): RecyclerView.ViewHolder(mView){
        fun populate(sale:Sales){
            val day = DateFormat.format("dd MMM,yyyy", Date(sale.date)) as String
            mView.findViewById<TextView>(R.id.date).text = day
            mView.findViewById<TextView>(R.id.gold).text = sale.gold.toString()
            mView.findViewById<TextView>(R.id.cash).text = sale.cash.toString()
            mView.findViewById<TextView>(R.id.total).text = sale.total.toString()
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