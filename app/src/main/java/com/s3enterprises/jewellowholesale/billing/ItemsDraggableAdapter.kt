package com.s3enterprises.jewellowholesale.billing

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.s3enterprises.jewellowholesale.R
import com.s3enterprises.jewellowholesale.database.models.Item
import java.util.*

class ItemsDraggableAdapter(private val list:List<Item>,
                            private val onSelected:(Item)->Unit,
                            private val onItemPositionsChanged:(List<Item>)->Unit) : RecyclerView.Adapter<ItemsDraggableAdapter.ViewHolder>() {

    var isdragged = false
    val simpleCallback = object: ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),ItemTouchHelper.ACTION_STATE_IDLE){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            Collections.swap(list,fromPosition,toPosition)
            notifyItemMoved(fromPosition,toPosition)
            isdragged = true
            return true
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            //On Drag finished
            if(!isdragged) return
            Log.i("DRAG","DETECTED")
            updatePositions()
            onItemPositionsChanged(list)
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { return }
    }

    fun updatePositions() = list.forEachIndexed{ index, item ->
        item.position = index
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