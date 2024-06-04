package com.welfare.blood.donation

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
import com.welfare.blood.donation.databinding.ItemPatientBinding

class PeopleAdapter(private val peopleList: List<Patient>) : RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {

    private var onItemClickListener: ((Patient) -> Unit)? = null

    fun setOnItemClickListener(listener: (Patient) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val binding = ItemPatientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PeopleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val person = peopleList[position]
        holder.bind(person)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(person)
        }
    }

    override fun getItemCount(): Int {
        return peopleList.size
    }

    class PeopleViewHolder(private val binding: ItemPatientBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(person: Patient) {
            binding.tvName.text = person.name
           // binding.tvEmail.text = person.email
            binding.tvAddress.text = person.address
            binding.tvBloodGroup.text = person.bloodGroup

//            Glide.with(binding.root.context)
//                .load(person.imageUrl)
//                .into(binding.ivProfileImage)

            binding.btnViewDetail.setOnClickListener {
                binding.root.context.startActivity(Intent(binding.root.context, PatientDetailActivity::class.java).apply {
                    putExtra("patientDetail", person)
                })
            }

            binding.btnCancel.setOnClickListener {

            }
        }
    }
}
