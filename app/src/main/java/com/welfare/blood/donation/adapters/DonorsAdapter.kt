package com.welfare.blood.donation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.databinding.DonorsItemBinding

class DonorsAdapter(private val donorsList: List<String>) :
    RecyclerView.Adapter<DonorsAdapter.DonorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
        val binding = DonorsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DonorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        holder.bind(donorsList[position])
    }

    override fun getItemCount(): Int {
        return donorsList.size
    }

    inner class DonorViewHolder(private val binding: DonorsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(donorId: String) {
            binding.donorName.text = donorId
        }
    }
}
