package com.s3enterprises.jewellowholesale.billing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.database.models.Item
import java.util.*

class ItemsDraggableAdapter(private val list:List<Item>,private val onSelected:(Item)->Unit) : RecyclerView.Adapter<ItemsDraggableAdapter.ViewHolder>() {

    val simpleCallback = object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP and ItemTouchHelper.DOWN,0){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            Collections.swap(list,fromPosition,toPosition)
            notifyItemMoved(fromPosition,toPosition)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }

    }

    inner class ViewHolder(mView: View):RecyclerView.ViewHolder(mView) {
        private val nameView = mView.findViewById<TextView>(R.id.item_name)

        fun setName(item: Item){
            nameView.text = item.name
            nameView.setOnClickListener { onSelected(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsDraggableAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.card_selection_list,parent,false))
    }

    override fun onBindViewHolder(holder: ItemsDraggableAdapter.ViewHolder, position: Int) {
        holder.setName(list[position])
    }

    override fun getItemCount() = list.size


}