package com.s3enterprises.jewellowholesale.items.itemsList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.database.models.Item

class ItemsAdapter(private val items:List<Item>) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>(),Filterable
{
    private var filteredItems = items

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view){
        private val nameView =view.findViewById<TextView>(R.id.item_name)
        fun populateCard(item: Item) {
            nameView.text = item.name
            view.setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.populateCard(filteredItems[position])
    }

    override fun getItemCount() = filteredItems.size

    override fun getFilter()= object: Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val cs = constraint.toString()
            filteredItems = if(cs.isEmpty()) items else {
                val filteredList = ArrayList<Item>()
                items.forEach{
                    if(it.name.toLowerCase().contains(cs.toLowerCase()))
                        filteredList.add(it)
                }
                filteredList
            }
            return FilterResults().also{ it.values = filteredItems }
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {
            if(results?.values != null){
                filteredItems = results.values as List<Item>
                notifyDataSetChanged()
            }
        }
    }
}