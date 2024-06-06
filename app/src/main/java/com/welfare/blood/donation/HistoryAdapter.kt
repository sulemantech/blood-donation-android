package com.welfare.blood.donation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.databinding.HistoryRecyclerviewBinding

class HistoryAdapter(private val historyItems: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(val binding: HistoryRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(historyItem: HistoryItem) {
            binding.nameText.text = historyItem.donorName
            binding.cityText.text = historyItem.city
            binding.bloodGroupText.text = historyItem.bloodGroup
            binding.btnText.text = historyItem.requestStatus
            binding.veText.text = historyItem.bloodType
            binding.dateText.text = historyItem.date
            binding.timeText.text = historyItem.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HistoryRecyclerviewBinding.inflate(inflater, parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyItems[position])
    }

    override fun getItemCount(): Int = historyItems.size
}
