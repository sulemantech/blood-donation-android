package com.welfare.blood.donation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.R
import com.welfare.blood.donation.models.BloodDonorModel

class BloodDonorAdapter(private val mList: List<BloodDonorModel>) : RecyclerView.Adapter<BloodDonorAdapter.ViewHolder>()   {
    private var bloodDonors = ArrayList<BloodDonorModel>()
    lateinit var context: Context

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        //val textView: TextView = itemView.findViewById(R.id.textView)

        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewContact: TextView = itemView.findViewById(R.id.textViewContact)
        val textViewBloodGroup: TextView = itemView.findViewById(R.id.textViewBloodGroup)
        val textViewCity: TextView = itemView.findViewById(R.id.textViewCity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.donor_card_view, parent, false)

        return BloodDonorAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bloodDonor = mList[position]


        holder.imageView.setImageResource(bloodDonor.getPic())

        //holder.textView.text = bloodDonor.getName()
        holder.textViewName.text = bloodDonor.getName()
        holder.textViewContact.text = bloodDonor.getContactNumber()
        holder.textViewBloodGroup.text = bloodDonor.getBloodType()
        holder.textViewCity.text = bloodDonor.getAddress()

    }
}