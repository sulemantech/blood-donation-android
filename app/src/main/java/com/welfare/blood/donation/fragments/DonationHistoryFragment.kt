package com.welfare.blood.donation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.adapters.DonationAdapter
import com.welfare.blood.donation.databinding.FragmentDonationHistoryBinding
import com.welfare.blood.donation.models.Donation

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
        adapter = DonationAdapter(donationList)
        binding.recyclerView.adapter = adapter

        fetchDonations()
    }

    private fun fetchDonations() {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            db.collection("donations")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener { result ->
                    donationList.clear()
                    for (document in result) {
                        val donation = document.toObject(Donation::class.java)
                        // Ensure 'status' field is fetched from Firestore
                        donation.status = document.getString("status") ?: "pending"
                        donationList.add(donation)
                    }
                    adapter.notifyDataSetChanged()
                    displayRequestCount(donationList.size)


                    // Show empty state if no donations
                    if (donationList.isEmpty()) {
                        binding.emptyView.visibility = View.VISIBLE
                    } else {
                        binding.emptyView.visibility = View.GONE
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("DonationHistoryFragment", "Error getting donations", exception)
                    // Show error message to the user
                    // Example: Snackbar or Toast
                }
        }
    }
        private fun displayRequestCount(count: Int) {
            binding.receivedDonationCount.text = "Requests Received: $count"
        }

    }

