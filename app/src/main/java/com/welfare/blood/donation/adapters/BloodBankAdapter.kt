package com.welfare.blood.donation.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.databinding.BloodBankRecyclerviewBinding
import com.welfare.blood.donation.models.BloodBankItem

class BloodBankAdapter(private val items: List<BloodBankItem>) : RecyclerView.Adapter<BloodBankAdapter.BloodBankViewHolder>() {

    inner class BloodBankViewHolder(val binding: BloodBankRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BloodBankItem) {
            binding.tvName.text = item.name
            binding.phone.text = item.phone
            binding.tvAddress.text = item.address

            binding.phone.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${item.phone}")
                it.context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BloodBankViewHolder {
        val binding = BloodBankRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BloodBankViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BloodBankViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
