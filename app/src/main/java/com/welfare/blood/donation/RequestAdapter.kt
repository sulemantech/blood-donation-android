package com.welfare.blood.donation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.databinding.HistoryRecyclerviewBinding
import com.welfare.blood.donation.models.Request

class RequestAdapter(private val requestList: List<Request>) :
    RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

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
            binding.bloodGroup.text = request.bloodType
            binding.statusTextView.text = "${request.status}"
            binding.bloodType.text = request.bloodType
            binding.requestDate.text = request.dateRequired
        }
    }
}
