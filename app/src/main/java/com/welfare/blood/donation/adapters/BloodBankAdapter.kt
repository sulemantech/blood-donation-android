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
            binding.textviewWebsite1.text = item.website1
            binding.tvAddress.text = item.address


            binding.phoneCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${item.phone}")
                }
                itemView.context.startActivity(intent)
            }

            binding.website.setOnClickListener {
                if (item.website1.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(item.website1)
                    }
                    it.context.startActivity(intent)
                }
            }

            binding.share.setOnClickListener {
                val shareText = "${item.name}\n${item.address}\nPhone: ${item.phone}\nWebsite: ${item.website1}"
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                it.context.startActivity(Intent.createChooser(intent, "Share via"))
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
