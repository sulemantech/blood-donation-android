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

            binding.phoneCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${item.phone}")
                }
                itemView.context.startActivity(intent)
            }

            binding.chat.setOnClickListener {
                val phoneNumber = item.phone
                val message = "Hello, this is a test message."

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("sms:$phoneNumber")
                    putExtra("sms_body", message)
                }
                itemView.context.startActivity(intent)
            }

            binding.share.setOnClickListener {
                val shareText = "Donor Name: ${item.name}\nLocation: ${item.address}\nContact: ${item.phone}"
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                val chooser = Intent.createChooser(intent, "Share via")
                itemView.context.startActivity(chooser)
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
