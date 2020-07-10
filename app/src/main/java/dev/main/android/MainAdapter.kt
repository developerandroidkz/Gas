package dev.main.android


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_zapravka.view.*

class MainAdapter (val items : ArrayList<History>, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick: ((History) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_zapravka, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var smena = items[position]
        holder.itemView.car_number.text = smena.car_number
        holder.itemView.car_name.text = smena.car_name
        holder.itemView.car_max_litr.text = smena.car_max_litr
        holder.itemView.litr.text = smena.litr+" л"
        holder.itemView.date.text = smena.date_time
        if(smena.type==1 && smena.zapravlen==1)
            holder.itemView.left_bar.setBackgroundColor(Color.parseColor("#1886f3"))
        else if(smena.type==2 && smena.zapravlen==1)
            holder.itemView.left_bar.setBackgroundColor(Color.parseColor("#18f355"))
        else if( smena.zapravlen==1)
            holder.itemView.left_bar.setBackgroundColor(Color.parseColor("#f3d018"))
        else
            holder.itemView.left_bar.setBackgroundColor(Color.parseColor("#cccccc"))


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

