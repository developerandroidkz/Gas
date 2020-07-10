package dev.main.android


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_smena.view.*

class ChooseSmenaAdapter (val items : ArrayList<Smena>, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick: ((Smena) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_smena, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var smena = items[position]
        holder.itemView.start.text = smena.start_date_string
        holder.itemView.end.text = smena.end_date_string
        holder.itemView.date_start.text = smena.start
        holder.itemView.date_end.text = smena.end
        when {
            smena.selected -> holder.itemView.card.setCardBackgroundColor(Color.parseColor("#e1f5fe"))
            smena.now -> holder.itemView.card.setCardBackgroundColor(Color.parseColor("#FFFDD6"))
            else -> holder.itemView.card.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
        }
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

