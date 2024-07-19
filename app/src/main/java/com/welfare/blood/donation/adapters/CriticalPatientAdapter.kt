package com.welfare.blood.donation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
            binding.requiredUnit.text = "${patient.requiredUnit} Units"
            binding.dateRequired.text = patient.dateRequired
        }
    }
}
