package dev.main.android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.pin_view.view.*

class ChooseAdapter (val items : ArrayList<Item>, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick: ((Item) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.choose_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var item = items[position]
        holder.itemView.name.text = item.name
        holder.itemView.address.text = item.address
    }

    override fun getItemCount(): Int {
        return items.size
    }
    inner class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(items[adapterPosition])
            }
        }
    }

}

