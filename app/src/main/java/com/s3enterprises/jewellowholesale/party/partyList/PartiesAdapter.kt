package com.s3enterprises.jewellowholesale.party.partyList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.database.models.Party

class PartiesAdapter(private val paties: List<Party>) :
    RecyclerView.Adapter<PartiesAdapter.ViewHolder>(), Filterable
{

    private var filteredPaties = paties

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val idView = view.findViewById<TextView>(R.id.party_id)!!
        private val nameView = view.findViewById<TextView>(R.id.party_name)!!
        private val numberView = view.findViewById<TextView>(R.id.party_number)!!
        fun populateCard(party: Party) = with(party){
            idView.text = pId.toString()
            nameView.text = name
            numberView.text = phoneNumber
            view.setOnClickListener {
                /*val intent = Intent(view.context, UpdateCustomerActivity::class.java)
                intent.putExtra("customer",customer)
                view.context.startActivity(intent)*/
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_party,parent,false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.populateCard(filteredPaties[position])
    }

    override fun getItemCount() = filteredPaties.size

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val cs = constraint.toString()
            filteredPaties = if(cs.isEmpty()) paties else {
                val filteredList = ArrayList<Party>()
                paties.forEach {
                    if(it.name.toLowerCase().contains(cs.toLowerCase()) || it.phoneNumber.contains(cs))
                        filteredList.add(it)
                }
                filteredList
            }
            return FilterResults().also { it.values = filteredPaties }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if(results?.values != null){
                filteredPaties = results.values as List<Party>
                notifyDataSetChanged()
            }
        }

    }

}