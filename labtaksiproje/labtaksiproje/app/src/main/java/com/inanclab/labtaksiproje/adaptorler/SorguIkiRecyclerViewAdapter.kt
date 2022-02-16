package com.inanclab.labtaksiproje.adaptorler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inanclab.labtaksiproje.R
import kotlinx.android.synthetic.main.sonucbir_row.view.*

class SorguIkiRecyclerViewAdapter(val tarihler : MutableList<String>,val yolcular : MutableList<String>, val mesafeler : MutableList<String>) : RecyclerView.Adapter<SorguIkiRecyclerViewAdapter.SorguIkiRecyclerViewViewHolder>() {
    class SorguIkiRecyclerViewViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SorguIkiRecyclerViewViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.sonucbir_row,parent,false)
        return SorguIkiRecyclerViewViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: SorguIkiRecyclerViewViewHolder, position: Int) {
        holder.itemView.rowbirtarih.text = tarihler.get(position) + " TARİHİNDE"
        holder.itemView.rowbiryolcusayisi.text = yolcular.get(position) + " YOLCU İLE"
        holder.itemView.rowbirmesafe.text = mesafeler.get(position) + " MİL"
    }

    override fun getItemCount(): Int {
        return tarihler.size
    }
}