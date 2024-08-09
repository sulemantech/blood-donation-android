package com.welfare.blood.donation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.R
import com.welfare.blood.donation.databinding.DonorsItemBinding
import com.welfare.blood.donation.models.Request
import com.welfare.blood.donation.models.User

class DonorAdapter(private val donorsList: MutableList<User>) : RecyclerView.Adapter<DonorAdapter.DonorViewHolder>() {

    inner class DonorViewHolder(private val binding: DonorsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.donorName.text = user.name
         //   binding.donorBloodGroup.text = request.bloodType
            val bloodTypeFull = getBloodTypeFull(user.bloodGroup)
            binding.donorBloodGroup.text =
                "${user.bloodGroup} ($bloodTypeFull) ${user.bloodGroup} Units Blood"
            binding.imageBloodGroup.setImageResource(getBloodGroupImage(user.bloodGroup))

            binding.donorLocation.text = user.location
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
