package com.welfare.blood.donation.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.databinding.BloodBankRecyclerviewBinding
import com.welfare.blood.donation.databinding.DonorsItemBinding
import com.welfare.blood.donation.models.User

class BloodBankAdapter(private val items: List<String>) : RecyclerView.Adapter<BloodBankAdapter.BloodBankViewHolder>() {

    inner class BloodBankViewHolder(val binding: BloodBankRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root){

    fun bind(user: User) {
            binding.tvName.text = user.name
            binding.phone.text = user.phone
            binding.tvAddress.text = user.location

            binding.phoneCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${user.phone}")
                }
                itemView.context.startActivity(intent)
            }

            binding.chat.setOnClickListener {
                // Handle in-app chat or redirect to a chat service
                // For example, if you have a chat activity, start it like this:
                // val intent = Intent(itemView.context, ChatActivity::class.java)
                // intent.putExtra("USER_ID", user.id)
                // itemView.context.startActivity(intent)

                // For now, just showing a message
                // Toast.makeText(itemView.context, "Chat with ${user.name}", Toast.LENGTH_SHORT).show()
            }

            binding.share.setOnClickListener {
                val shareText = "Donor Name: ${user.name}\nBlood Group: ${user.bloodGroup}\nLocation: ${user.location}\nContact: ${user.phone}"
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
        holder.binding.tvName.text = items[position]
        holder.binding.tvAddress.text = items[position]
        holder.binding.tvBloodGroup.text = items[position]
    }

    override fun getItemCount():
            Int = items.size.coerceAtMost(2)

}
