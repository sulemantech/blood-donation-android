package com.welfare.blood.donation

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.databinding.ItemPatientBinding

class UserAdapter(
    private val context: Context,
    private var userList: List<User>
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(private val binding: ItemPatientBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.tvName.text = user.name
            binding.tvAddress.text = user.location
            binding.tvBloodGroup.text = user.bloodGroup
            binding.phone.text =user.phone

            // Set the blood group image
            binding.imageBloodGroup.setImageResource(getBloodGroupImageResource(user.bloodGroup))

            // Set the required unit text
            binding.requiredUnit.text = "${user.bloodGroup} (${getBloodGroupFullName(user.bloodGroup)})"

            binding.btnViewDetail.setOnClickListener {
                showUserDetailsDialog(user)
            }

//            binding.btnSendRequest.setOnClickListener {
//                // Handle send request action
//                showRequestConfirmationDialog(user)
//            }
        }

        private fun showUserDetailsDialog(user: User) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_patient_detail, null)
            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setTitle("User Details")
                .setMessage("Name: ${user.name}\nLocation: ${user.location}\nBlood Group: ${user.bloodGroup}")
                .setPositiveButton("Close") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }

        @SuppressLint("SuspiciousIndentation")
        private fun showRequestConfirmationDialog(user: User) {
            val intent = Intent(context, CreateRequestActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemPatientBinding.inflate(LayoutInflater.from(context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    fun updateData(newUsers: List<User>) {
        userList = newUsers
        notifyDataSetChanged()
    }

    private fun getBloodGroupImageResource(bloodGroup: String): Int {
        return when (bloodGroup) {
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

    private fun getBloodGroupFullName(bloodGroup: String): String {
        return when (bloodGroup) {
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
}//i want to add phone instead of bloog group
