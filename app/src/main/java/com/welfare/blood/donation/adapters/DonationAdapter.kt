package com.welfare.blood.donation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.databinding.ItemDonationBinding
import com.welfare.blood.donation.models.Donation

class DonationAdapter(private val donationList: List<Donation>) :
    RecyclerView.Adapter<DonationAdapter.DonationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val binding = ItemDonationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DonationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        holder.bind(donationList[position])
    }

    override fun getItemCount(): Int {
        return donationList.size
    }

    inner class DonationViewHolder(private val binding: ItemDonationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(donation: Donation) {
            binding.recipientName.text = donation.recipientName
            binding.location.text = donation.location
            binding.bloodGroup.text = donation.bloodType
            binding.status.text = donation.status // Bind status field
            binding.bloodType.text = donation.bloodType
            binding.donationDate.text = donation.dateDonated
        }
    }
}
