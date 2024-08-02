package com.welfare.blood.donation.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.CreateRequestActivity
import com.welfare.blood.donation.adapters.ReceivedRequestAdapter
import com.welfare.blood.donation.databinding.FragmentReceivedRequestsBinding
import com.welfare.blood.donation.models.ReceivedRequest
import java.text.SimpleDateFormat
import java.util.Locale

class ReceivedRequestsFragment : Fragment() {

    private lateinit var binding: FragmentReceivedRequestsBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var requestList: MutableList<ReceivedRequest>
    private lateinit var adapter: ReceivedRequestAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReceivedRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        requestList = mutableListOf()
        adapter = ReceivedRequestAdapter(requestList) // Initialize the adapter
        binding.recyclerView.adapter = adapter

        fetchRequests()
        binding.createRequest.setOnClickListener {
            val intent = Intent(requireContext(), CreateRequestActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchRequests() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.w("ReceivedRequestsFragment", "User not logged in")
            return
        }

        db.collection("requests")
            .whereNotEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                requestList.clear()
                for (document in documents) {
                    try {
                        val requiredUnitField = document.get("requiredUnit")
                        Log.d("FirestoreDataType", "requiredUnit type: ${requiredUnitField?.javaClass?.name}")

                        val requiredUnit = when (requiredUnitField) {
                            is Number -> requiredUnitField.toInt()
                            is String -> requiredUnitField.toIntOrNull() ?: 0
                            else -> 0
                        }

                        val receivedRequest = ReceivedRequest(
                            patientName = document.getString("patientName") ?: "",
                            age = document.getLong("age")?.toInt() ?: 0,
                            bloodType = document.getString("bloodType") ?: "",
                            requiredUnit = requiredUnit,
                            dateRequired = document.getString("dateRequired") ?: "",
                            hospital = document.getString("hospital") ?: "",
                            location = document.getString("location") ?: "",
                            bloodFor = document.getString("bloodFor") ?: "",
                            userId = document.getString("userId") ?: "",
                            recipientId = document.getString("recipientId") ?: "",
                            status = document.getString("status") ?: "pending",
                            critical = document.getBoolean("critical") ?: false
                        )
                        requestList.add(receivedRequest)
                    } catch (e: Exception) {
                        Log.e("ReceivedRequestsFragment", "Error parsing request document", e)
                    }
                }
                requestList.sortByDescending {
                    dateFormat.parse(it.dateRequired)
                }
                adapter.notifyDataSetChanged()
                displayRequestCount(requestList.size)
            }
            .addOnFailureListener { e ->
                Log.w("ReceivedRequestsFragment", "Error fetching requests", e)
            }
    }

    private fun displayRequestCount(count: Int) {
        binding.receivedRequestCount.text = "Total Requests: $count"
    }
}//i want add textview for critical if user check critical checkbox than i want both critical and non critical request fetched
