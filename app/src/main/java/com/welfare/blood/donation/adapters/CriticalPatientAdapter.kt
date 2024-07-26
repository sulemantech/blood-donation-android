package com.welfare.blood.donation.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.CriticalPatientsListActivity
import com.welfare.blood.donation.databinding.CriticalPatientItemBinding
import com.welfare.blood.donation.models.CriticalPatient

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

            val bloodTypeFull = getBloodTypeFull(patient.bloodType)
            binding.requiredUnit.text = "${patient.bloodType} ($bloodTypeFull) ${patient.requiredUnit} Units Blood"

            binding.dateRequired.text = patient.dateRequired

            binding.donateNow.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, CriticalPatientsListActivity::class.java)
                // Pass any necessary data to the DonateBloodActivity
              //  intent.putExtra("patientId", patient.id) // Assuming each patient has a unique ID
                context.startActivity(intent)
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
