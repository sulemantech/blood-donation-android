package com.welfare.blood.donation.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.CriticalPatientsListActivity
import com.welfare.blood.donation.databinding.CriticalPatientItemBinding
import com.welfare.blood.donation.models.CriticalPatient
import com.welfare.blood.donation.R

class CriticalPatientAdapter(private val patients: List<CriticalPatient>) :
    RecyclerView.Adapter<CriticalPatientAdapter.CriticalPatientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CriticalPatientViewHolder {
        val binding = CriticalPatientItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CriticalPatientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CriticalPatientViewHolder, position: Int) {
        holder.bind(patients[position])
    }

    override fun getItemCount(): Int {
        return patients.size
    }

    inner class CriticalPatientViewHolder(private val binding: CriticalPatientItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(patient: CriticalPatient) {
            binding.patientName.text = patient.patientName
            binding.bloodGroup.text = patient.bloodType
            binding.condition.text = patient.status
            binding.contactInfo.text = patient.location
            binding.hospital.text = patient.hospital

            val bloodTypeFull = getBloodTypeFull(patient.bloodType)
            binding.requiredUnit.text = "${patient.bloodType} ($bloodTypeFull) ${patient.requiredUnit} Units Blood"
            binding.dateRequired.text = patient.dateRequired
            binding.imageBloodGroup.setImageResource(getBloodGroupImage(patient.bloodType))

            // Set visibility of the critical status
            if (patient.critical) {
                binding.criticalStatus.visibility = View.VISIBLE
            } else {
                binding.criticalStatus.visibility = View.GONE
            }

            binding.donateNow.setOnClickListener {
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
}
