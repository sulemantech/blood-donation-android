package com.welfare.blood.donation.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.adapters.DonationAdapter
import com.welfare.blood.donation.databinding.FragmentDonationHistoryBinding
import com.welfare.blood.donation.models.Donation
import com.welfare.blood.donation.models.Request
import java.text.SimpleDateFormat
import java.util.Locale

class DonationHistoryFragment : Fragment() {

    private lateinit var binding: FragmentDonationHistoryBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var donationList: MutableList<Donation>
    private lateinit var adapter: DonationAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDonationHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        donationList = mutableListOf()

        adapter = DonationAdapter(donationList, ::onEditClick, ::onDeleteClick)
        binding.recyclerView.adapter = adapter

        fetchDonations()
    }
    private fun fetchDonations() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            db.collection("donations")
                .whereEqualTo("userId", currentUserId)
               // .whereEqualTo("isDeleted", true)
                .get()
                .addOnSuccessListener { result ->
                    donationList.clear()
                    for (document in result) {
                        val donation = document.toObject(Donation::class.java).apply {
                            id = document.id
                        }
                        if (document.contains("isDeleted") && document.getBoolean("isDeleted") == true) {
                            continue
                        }

                        donation.status = document.getString("status") ?: "pending"
                        donationList.add(donation)
                    }
                    donationList.sortByDescending {
                        dateFormat.parse(it.date)
                    }

                    adapter.notifyDataSetChanged()
                    displayDonationCount(donationList.size)

                    if (donationList.isEmpty()) {
                        binding.emptyView.visibility = View.VISIBLE
                    } else {
                        binding.emptyView.visibility = View.GONE
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("DonationHistoryFragment", "Error getting donations", exception)
                }
        }
    }
    private fun displayDonationCount(count: Int) {
        binding.receivedDonationCount.text = "Total Donations: $count"
    }

    private fun onEditClick(donation: Donation) {
        Toast.makeText(requireContext(), "Edit clicked for ${donation.recipientName}", Toast.LENGTH_SHORT).show()
    }
    private fun onDeleteClick(donation: Donation) {
        showDeleteConfirmationDialog(donation)
    }

    private fun showDeleteConfirmationDialog(donation: Donation) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Donation")
        builder.setMessage("Are you sure you want to delete this donation?")

        builder.setPositiveButton("Delete") { dialog: DialogInterface, _: Int ->
            deleteDonation(donation) // Proceed with delete
        }

        builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            dialog.dismiss() // Close the dialog
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun deleteDonation(donation: Donation) {
        val donationId = donation.id
        if (donationId != null) {
            db.collection("donations").document(donationId)
                .update("isDeleted", true) // Mark as deleted or delete it
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Donation deleted successfully", Toast.LENGTH_SHORT).show()
                    donationList.removeAll { it.id == donation.id }
                    adapter.notifyDataSetChanged()
                    displayDonationCount(donationList.size)
                }
                .addOnFailureListener { e ->
                    Log.w("DonationHistoryFragment", "Error deleting donation", e)
                    Toast.makeText(requireContext(), "Error deleting donation", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.w("DonationHistoryFragment", "Donation ID is null or invalid")
        }
    }
}
