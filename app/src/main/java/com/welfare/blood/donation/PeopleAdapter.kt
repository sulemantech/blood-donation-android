package com.welfare.blood.donation

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.welfare.blood.donation.databinding.DialogPatientDetailBinding
import com.welfare.blood.donation.databinding.ItemPatientBinding

class PeopleAdapter(private val peopleList: List<Patient>) : RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val binding = ItemPatientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PeopleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val person = peopleList[position]
        holder.bind(person)
    }

    override fun getItemCount(): Int {
        return peopleList.size
    }

    class PeopleViewHolder(private val binding: ItemPatientBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(person: Patient) {
            binding.tvName.text = person.name
            binding.tvEmail.text = person.email
            binding.tvAddress.text = person.address
            binding.tvBloodGroup.text = person.bloodGroup

            Glide.with(binding.root.context)
                .load(person.imageUrl)
                .into(binding.ivProfileImage)

            binding.btnViewDetail.setOnClickListener {
                showPatientDetailDialog(binding.root.context, person)
            }

            binding.btnCancel.setOnClickListener {
                // Implement cancellation logic if needed
            }
        }

        private fun showPatientDetailDialog(context: Context, patient: Patient) {
            val dialogBinding = DialogPatientDetailBinding.inflate(LayoutInflater.from(context))

            val dialog = AlertDialog.Builder(context)
                .setView(dialogBinding.root)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()

            dialogBinding.tvName.text = patient.name
            dialogBinding.tvEmail.text = patient.email
            dialogBinding.tvAddress.text = patient.address
            dialogBinding.tvBloodGroup.text = patient.bloodGroup

            Glide.with(context)
                .load(patient.imageUrl)
                .into(dialogBinding.ivProfileImage)

            dialog.show()
        }
    }
}
