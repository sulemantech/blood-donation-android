package com.welfare.blood.donation.adapters

import android.app.AlertDialog
import android.graphics.Color
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
import com.welfare.blood.donation.models.ReceivedRequest

class CriticalPatientAdapter(
    private val patients: MutableList<CriticalPatient>,
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

    fun updatePatients(newPatients: List<CriticalPatient>) {
        patients.clear()
        patients.addAll(newPatients)
        notifyDataSetChanged()
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
            checkDonationStatus(patient)

            binding.donateNow.setOnClickListener {
                showConfirmationDialog(patient)
            }
            binding.completed.setOnClickListener {
                Toast.makeText(binding.root.context, "You have already sent a donation request.", Toast.LENGTH_SHORT).show()
            }
        }

        private fun checkDonationStatus(patient: CriticalPatient) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                return
            }

            val currentUserId = currentUser.uid
            val db = FirebaseFirestore.getInstance()
            val documentRef = db.collection("requests").document(patient.id)

            documentRef.get().addOnSuccessListener { document ->
                val donors = document.get("donors") as? List<String> ?: emptyList()
                if (donors.contains(currentUserId)) {
                    binding.donateNow.visibility = View.GONE
                    binding.completed.visibility = View.VISIBLE
                } else {
                    binding.donateNow.visibility = View.VISIBLE
                    binding.completed.visibility = View.GONE
                }
            }.addOnFailureListener {
                Log.e("ReceivedRequestAdapter", "Failed to check donation status", it)
            }
        }

        private fun showConfirmationDialog(patient: CriticalPatient) {
            val dialog = AlertDialog.Builder(binding.root.context)
                .setTitle("Confirm Donation")
                .setMessage("Do you want to donate blood?")
                .setPositiveButton("Yes") { _, _ ->
                    addDonor(patient)
                }
                .setNegativeButton("No", null)
                .show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.RED)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.RED)
        }

        private fun addDonor(patient: CriticalPatient) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Toast.makeText(binding.root.context, "User not logged in", Toast.LENGTH_SHORT).show()
                return
            }

            val currentUserId = currentUser.uid
            val db = FirebaseFirestore.getInstance()

            val documentId = patient.id
            Log.d("CriticalPatientAdapter", "Attempting to update document ID: $documentId")

            if (documentId.isNullOrEmpty()) {
                Toast.makeText(binding.root.context, "Invalid document ID", Toast.LENGTH_SHORT).show()
                return
            }

            val documentRef = db.collection("requests").document(documentId)

            documentRef.update("donors", FieldValue.arrayUnion(currentUserId))
                .addOnSuccessListener {
                    Toast.makeText(binding.root.context, "Donation request sent", Toast.LENGTH_SHORT).show()
                    binding.donateNow.visibility = View.GONE
                    binding.completed.visibility = View.VISIBLE
                }
                .addOnFailureListener { e ->
                    Log.e("CriticalPatientAdapter", "Failed to update donors for document ID $documentId", e)
                    Toast.makeText(binding.root.context, "Failed to send request", Toast.LENGTH_SHORT).show()
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
}
