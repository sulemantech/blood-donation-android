package com.welfare.blood.donation.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.R
import com.welfare.blood.donation.databinding.CommunityDonorsBinding
import com.welfare.blood.donation.models.CommunityDonors

class CommunityAdapter(
    private val donorList: List<CommunityDonors>,
    private val onEditClick: (CommunityDonors) -> Unit,
    private val onDeleteClick: (CommunityDonors) -> Unit
) : RecyclerView.Adapter<CommunityAdapter.DonorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
        val binding = CommunityDonorsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DonorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        val donor = donorList[position]
        holder.bind(donor)
    }

    override fun getItemCount(): Int = donorList.size

    inner class DonorViewHolder(private val binding: CommunityDonorsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(communityDonors: CommunityDonors) {
            binding.tvName.text = communityDonors.name
            binding.phone.text = communityDonors.phone
            binding.imageBloodGroup.setImageResource(getBloodGroupImage(communityDonors.bloodGroup))
            binding.tvAddress.text = communityDonors.location

            binding.edit.setOnClickListener {
                onEditClick(communityDonors)
            }
            binding.delete.setOnClickListener {
                onDeleteClick(communityDonors)
            }

            binding.phoneCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${communityDonors.phone}")
                }
                itemView.context.startActivity(intent)
            }
            binding.chat.setOnClickListener {
                val phoneNumber = communityDonors.phone
                val message = "Hello, this is a test message."

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("sms:$phoneNumber")
                    putExtra("sms_body", message)
                }
                itemView.context.startActivity(intent)
            }

            binding.share.setOnClickListener {
                val shareText = "Donor Name: ${communityDonors.name}\nBlood Group: ${communityDonors.bloodGroup}\nLocation: ${communityDonors.location}\nContact: ${communityDonors.phone}"
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                val chooser = Intent.createChooser(intent, "Share via")
                itemView.context.startActivity(chooser)
            }
        }

        private fun getBloodGroupImage(bloodType: String): Int {
            return when (bloodType) {
                "A+" -> R.drawable.ic_a_plus
                "A-" -> R.drawable.ic_a_minus
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
