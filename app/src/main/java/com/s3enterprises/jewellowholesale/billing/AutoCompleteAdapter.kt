package com.s3enterprises.jewellowholesale.billing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.database.models.Item
import com.s3enterprises.jewellowholesale.database.models.Party

class AutoCompleteAdapter(ctx: Context, private val resource:Int, private val parties:List<Party>)
    :ArrayAdapter<Party>(ctx,resource,ArrayList<Party>()),Filterable {

    private val filter = object :Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val suggestions = if(constraint.isNullOrBlank()) parties else {
                val cs = constraint.toString().trim()
                val filteredList = ArrayList<Party>()
                parties.forEach{
                    if(it.name.lowercase().contains(cs.lowercase()) || it.phoneNumber.contains(cs))
                        filteredList.add(it)
                }
                filteredList
            }
            return FilterResults().apply {
                values = suggestions ; count = suggestions.size
            }
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults) {
            if(results.values == null) return
            clear()
            addAll(results.values as List<Party>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any): CharSequence {
            return (resultValue as Party).name
        }
    }

    override fun getFilter() = filter

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: LayoutInflater.from(context).inflate(resource,parent,false)
        val party = getItem(position)!!
        v.findViewById<TextView>(R.id.party_name).text = party.name
        v.findViewById<TextView>(R.id.party_number).text = party.phoneNumber
        return v
    }

}

