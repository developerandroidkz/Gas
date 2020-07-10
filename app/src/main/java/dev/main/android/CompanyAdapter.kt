package dev.main.android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_about_smena.view.*


class CompanyAdapter (val items : ArrayList<Company>, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick: ((Company) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_about_smena, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var company = items[position]
        holder.itemView.company_name.text = company.name
        holder.itemView.litr.text = company.litr
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