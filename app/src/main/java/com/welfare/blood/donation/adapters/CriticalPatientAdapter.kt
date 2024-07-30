package com.welfare.blood.donation.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.CriticalPatientsListActivity
import com.welfare.blood.donation.databinding.CriticalPatientItemBinding
import com.welfare.blood.donation.models.CriticalPatient
import com.welfare.blood.donation.R // Add this import for accessing resources

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
            binding.condition.text = patient.status // Assuming `status` is used to describe the condition
            binding.contactInfo.text = patient.location // Or another appropriate field
            binding.hospital.text = patient.hospital

            // Display blood group with units in desired format
            val bloodTypeFull = getBloodTypeFull(patient.bloodType)
            binding.requiredUnit.text = "${patient.bloodType} ($bloodTypeFull) ${patient.requiredUnit} Units Blood"

            binding.dateRequired.text = patient.dateRequired

            // Set the image for the blood group
            binding.imageBloodGroup.setImageResource(getBloodGroupImage(patient.bloodType))

            // Set click listener for the "Donate Now" button
            binding.donateNow.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, CriticalPatientsListActivity::class.java)
                // Pass any necessary data to the DonateBloodActivity
               // intent.putExtra("patientId", patient.id) // Assuming each patient has a unique ID
                context.startActivity(intent)
            }
        }

        private fun getBloodGroupImage(bloodType: String): Int {
            return when (bloodType) {
                "A+" -> R.drawable.ic_a
                "A-" -> R.drawable.ic_a_plus
                "B+" -> R.drawable.ic_b_plus
                "B-" -> R.drawable.ic_b_minus
                "AB+" -> R.drawable.ic_ab_plus
                "AB-" -> R.drawable.ic_ab_minus
                "O+" -> R.drawable.ic_o_plus
                "O-" -> R.drawable.ic_o_minus
                else -> R.drawable.blood_droplet // Add a default image if necessary

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
