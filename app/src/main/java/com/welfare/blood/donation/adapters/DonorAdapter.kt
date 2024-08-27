package com.welfare.blood.donation.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.R
import com.welfare.blood.donation.databinding.DonorsItemBinding
import com.welfare.blood.donation.models.User

class DonorAdapter(private val donorsList: MutableList<User>) : RecyclerView.Adapter<DonorAdapter.DonorViewHolder>() {

    inner class DonorViewHolder(private val binding: DonorsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.donorName.text = user.name
            val bloodTypeFull = getBloodTypeFull(user.bloodGroup)
            binding.donorBloodGroup.text =
                "${user.bloodGroup} ($bloodTypeFull)"
            binding.imageBloodGroup.setImageResource(getBloodGroupImage(user.bloodGroup))
            binding.donorPhone.text = user.phone
            binding.donorLocation.text = user.location

            binding.phoneCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${user.phone}")
                }
                itemView.context.startActivity(intent)
            }

            binding.chat.setOnClickListener {
                val phoneNumber = user.phone
                val message = "Hello, this is a test message."

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("sms:$phoneNumber")
                    putExtra("sms_body", message)
                }
                itemView.context.startActivity(intent)
            }

            binding.share.setOnClickListener {
                val shareText = "Donor Name: ${user.name}\nBlood Group: ${user.bloodGroup}\nLocation: ${user.location}\nContact: ${user.phone}"
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                val chooser = Intent.createChooser(intent, "Share via")
                itemView.context.startActivity(chooser)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
        val binding = DonorsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DonorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        holder.bind(donorsList[position])
    }

    override fun getItemCount(): Int = donorsList.size

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
            else -> R.drawable.drop
        }
    }

    private fun getBloodTypeFull(bloodType: String): String {
        return when (bloodType) {
            "A+" -> "A Positive"
            "A-" -> "A Negative"
            "B+" -> "B Positive"
            "B-" -> "B Negative"
            "AB+" -> "AB Positive"
            "AB-" -> "AB Negative"
            "O+" -> "O Positive"
            "O-" -> "O Negative"
            else -> "Unknown"
        }
    }
}
