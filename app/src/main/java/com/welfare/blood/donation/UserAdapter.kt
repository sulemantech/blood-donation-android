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

            binding.btnViewDetail.setOnClickListener {
                showUserDetailsDialog(user)
            }

            binding.btnSendRequest.setOnClickListener {
                // Handle send request action
                showRequestConfirmationDialog(user)
            }
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
//            val dialog = AlertDialog.Builder(context)
//                .setTitle("Send Request")
//                .setMessage("Send a request to ${user.name}?")
//                .setPositiveButton("Send") { dialog, _ ->
//                    // Handle request sending logic here
//                    dialog.dismiss()
//                    // Add your logic here to handle the request sending action
//                    // For example, you can initiate a network call to send a request to the user
//                    // or perform any other action based on your application's requirements.
//                }
//                .setNegativeButton("Cancel") { dialog, _ ->
//                    dialog.dismiss()
//                }
//                .create()
//            dialog.show()
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
}
