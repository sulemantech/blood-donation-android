package com.welfare.blood.donation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PeopleAdapter(private val peopleList: List<String>) : RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return PeopleViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        holder.tvPerson.text = peopleList[position]
    }

    override fun getItemCount(): Int {
        return peopleList.size
    }

    class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPerson: TextView = itemView.findViewById(android.R.id.text1)
    }
}
