package com.s3enterprises.jewellowholesale.items.itemsList

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.database.models.Item
import com.s3enterprises.jewellowholesale.items.addItem.AddItem

class ItemsAdapter(private val items:List<Item>) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>(),Filterable
{
    private var filteredItems = items

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view){
        private val nameView =view.findViewById<TextView>(R.id.item_name)
        private val rateView =view.findViewById<TextView>(R.id.item_rate)
        fun populateCard(item: Item) {
            nameView.text = item.name
            rateView.text = item.rate.toString()
            view.setOnClickListener {
                val i = Intent(view.context,AddItem::class.java)
                i.putExtra("item",item)
                view.context.startActivity(i)
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
                    if(it.name.lowercase().contains(cs.lowercase()))
                        filteredList.add(it)
                }
                filteredList
            }
            return FilterResults().apply{ values = filteredItems }
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {
            if(results?.values != null){
                filteredItems = results.values as List<Item>
                notifyDataSetChanged()
            }
        }
    }
}