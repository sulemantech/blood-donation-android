package com.welfare.blood.donation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.databinding.HistoryRecyclerviewBinding
import com.welfare.blood.donation.models.Request

class RequestAdapter(
    private val requestList: List<Request>,
    private val onEditClick: (Request) -> Unit,
    private val onDeleteClick: (Request) -> Unit
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding = HistoryRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(requestList[position])
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    inner class RequestViewHolder(private val binding: HistoryRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(request: Request) {
            binding.donorName.text = request.patientName
            binding.location.text = request.location
            val bloodTypeFull = getBloodTypeFull(request.bloodType)
            binding.requiredUnit.text = "${request.bloodType} ($bloodTypeFull) ${request.requiredUnit} Units Blood"
            binding.imageBloodGroup.setImageResource(getBloodGroupImage(request.bloodType))
            binding.bloodType.text = request.bloodType
            binding.requestDate.text = request.dateRequired

            // Display critical indicator if the request is marked as critical
            binding.criticalIndicator.visibility = if (request.critical) View.VISIBLE else View.GONE

            // Set up the edit and delete button click listeners
            binding.edit.setOnClickListener {
                onEditClick(request)
            }
            binding.delete.setOnClickListener {
                onDeleteClick(request)
            }
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
