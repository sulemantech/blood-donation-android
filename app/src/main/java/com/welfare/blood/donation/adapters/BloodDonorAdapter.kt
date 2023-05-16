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
    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.blood_donor_card_view, parent, false)

        return BloodDonorAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.bloodDonors.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bloodDonor = mList[position]

        // sets the image to the imageview from our itemHolder class
        holder.imageView.setImageResource(bloodDonor.getPic())

        // sets the text to the textview from our itemHolder class
        holder.textView.text = bloodDonor.getName()
    }
}