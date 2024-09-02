package com.welfare.blood.donation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.R
import com.welfare.blood.donation.databinding.ItemDonationBinding
import com.welfare.blood.donation.models.Donation

class DonationAdapter(
    private val donationList: List<Donation>,
    private val onEditClick: (Donation) -> Unit,
    private val onDeleteClick: (Donation) -> Unit
) : RecyclerView.Adapter<DonationAdapter.DonationViewHolder>() {

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
            binding.imageBloodGroup.setImageResource(getBloodGroupImage(donation.bloodType))
            binding.bloodGroup.text = donation.bloodType
            binding.bloodGroup.text = "${donation.bloodType} Positive"

            binding.donationDate.text = donation.date

            binding.edit.setOnClickListener {
                onEditClick(donation)
            }
            binding.delete.setOnClickListener {
                onDeleteClick(donation)
            }
        }

        private fun getBloodGroupImage(bloodType: String): Int {
            return when (bloodType) {
                "A+" -> R.drawable.ic_a_minus
                "A-" -> R.drawable.ic_a_plus
                "B+" -> R.drawable.ic_b_plus
                "B-" -> R.drawable.ic_b_minus
                "AB+" -> R.drawable.ic_ab_plus
                "AB-" -> R.drawable.ic_ab_minus
                "O+" -> R.drawable.ic_o_plus
                "O-" -> R.drawable.ic_o_minus
                else -> R.drawable.blood_droplet
            }
        }
    }
}
