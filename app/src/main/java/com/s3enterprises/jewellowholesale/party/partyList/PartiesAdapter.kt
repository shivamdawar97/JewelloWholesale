package com.s3enterprises.jewellowholesale.party.partyList

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.bills.BillsActivity
import com.s3enterprises.jewellowholesale.database.models.Party
import com.s3enterprises.jewellowholesale.party.addParty.AddParty

class PartiesAdapter(private val parties: List<Party>) :
    RecyclerView.Adapter<PartiesAdapter.ViewHolder>(), Filterable
{

    private var filteredParties = parties

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val nameView = view.findViewById<TextView>(R.id.party_name)!!
        private val numberView = view.findViewById<TextView>(R.id.party_number)!!
        private val viewBillsView = view.findViewById<TextView>(R.id.party_view_bills)!!
        fun populateCard(party: Party) = with(party){
            nameView.text = name
            numberView.text = phoneNumber
            view.setOnClickListener {
                val intent = Intent(view.context, AddParty::class.java)
                intent.putExtra("party",party)
                view.context.startActivity(intent)
            }
            viewBillsView.setOnClickListener {
                val intent = Intent(view.context, BillsActivity::class.java)
                intent.putExtra("pName",party.name)
                view.context.startActivity(intent)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_party,parent,false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.populateCard(filteredParties[position])
    }

    override fun getItemCount() = filteredParties.size

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val cs = constraint.toString()
            filteredParties = if(cs.isEmpty()) parties else {
                val filteredList = ArrayList<Party>()
                parties.forEach {
                    if(it.name.toLowerCase().contains(cs.toLowerCase()) || it.phoneNumber.contains(cs))
                        filteredList.add(it)
                }
                filteredList
            }
            return FilterResults().also { it.values = filteredParties }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if(results?.values != null){
                filteredParties = results.values as List<Party>
                notifyDataSetChanged()
            }
        }

    }

}