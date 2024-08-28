package com.welfare.blood.donation.adapters

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.R
import com.welfare.blood.donation.databinding.CriticalPatientItemBinding
import com.welfare.blood.donation.models.CriticalPatient

class CriticalPatientAdapter(
    private val patients: List<CriticalPatient>,
    private val listener: OnPatientClickListener? = null
) : RecyclerView.Adapter<CriticalPatientAdapter.CriticalPatientViewHolder>() {

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
            binding.condition.text = patient.status
            binding.contactInfo.text = patient.location

            val bloodTypeFull = getBloodTypeFull(patient.bloodType)
            binding.requiredUnit.text = "${patient.bloodType} ($bloodTypeFull) ${patient.requiredUnit} Units Blood"
            binding.dateRequired.text = patient.dateRequired
            binding.imageBloodGroup.setImageResource(getBloodGroupImage(patient.bloodType))

            binding.criticalStatus.visibility = if (patient.critical) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                listener?.onPatientClick(patient)
            }
            binding.donateNow.setOnClickListener {
                showConfirmationDialog(patient)
            }
        }

        private fun showConfirmationDialog(patient: CriticalPatient) {
            AlertDialog.Builder(binding.root.context)
                .setTitle("Confirm Donation")
                .setMessage("Do you want to donate?")
                .setPositiveButton("Yes") { _, _ ->
                    addDonor(patient)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        private fun addDonor(patient: CriticalPatient) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Toast.makeText(binding.root.context, "User not logged in", Toast.LENGTH_SHORT).show()
                return
            }

            val currentUserId = currentUser.uid
            val db = FirebaseFirestore.getInstance()

            Log.d("CriticalPatientAdapter", "Document ID: ${patient.id}")

            if (patient.id.isNullOrEmpty()) {
                Toast.makeText(binding.root.context, "Invalid document ID", Toast.LENGTH_SHORT).show()
                return
            }

            val documentRef = db.collection("requests").document(patient.id!!)

            documentRef.update("donors", FieldValue.arrayUnion(currentUserId))
                .addOnSuccessListener {
                    Toast.makeText(binding.root.context, "Requested", Toast.LENGTH_SHORT).show()
                    binding.donateNow.visibility = View.GONE
                    binding.completed.visibility = View.VISIBLE
                }
                .addOnFailureListener { e ->
                    Log.e("CriticalPatientAdapter", "Failed to update donors", e)
                    Toast.makeText(binding.root.context, "Failed to donate", Toast.LENGTH_SHORT).show()
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

    interface OnPatientClickListener {
        fun onPatientClick(patient: CriticalPatient)
    }
}//main chahiti hun k jab donate now py click kren to ek dialog show jis me ho do you want to donate blood yes or no to yes krny py request send ho jaye us user ko
