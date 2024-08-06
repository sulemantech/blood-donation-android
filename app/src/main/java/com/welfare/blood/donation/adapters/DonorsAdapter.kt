package com.welfare.blood.donation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.R
import com.welfare.blood.donation.models.Donors

class DonorsAdapter(private val donorList: List<Donors>) : RecyclerView.Adapter<DonorsAdapter.DonorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.donors_item, parent, false)
        return DonorViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        val donor = donorList[position]
        holder.bind(donor)
    }

    override fun getItemCount() = donorList.size

    class DonorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val donorName: TextView = itemView.findViewById(R.id.donorName)
        private val donorBloodGroup: TextView = itemView.findViewById(R.id.donorBloodGroup)
        private val donorPhone: TextView = itemView.findViewById(R.id.donorPhone)
        private val donorEmail: TextView = itemView.findViewById(R.id.donorEmail)

        fun bind(donor: Donors) {
            donorName.text = donor.name
            donorBloodGroup.text = "Blood Group: ${donor.bloodType}"
            donorPhone.text = "Phone: ${donor.phone}"
            donorEmail.text = "Email: ${donor.email}"
        }
    }
}
