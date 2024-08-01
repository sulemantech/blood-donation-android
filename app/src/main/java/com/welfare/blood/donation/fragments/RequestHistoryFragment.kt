package com.welfare.blood.donation.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.CreateRequestActivity
import com.welfare.blood.donation.RequestAdapter
import com.welfare.blood.donation.databinding.FragmentRequestHistoryBinding
import com.welfare.blood.donation.models.Request

class RequestHistoryFragment : Fragment() {

    private lateinit var binding: FragmentRequestHistoryBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var requestList: MutableList<Request>
    private lateinit var adapter: RequestAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRequestHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        requestList = mutableListOf()
        adapter = RequestAdapter(requestList, ::onEditClick, ::onDeleteClick)
        binding.recyclerView.adapter = adapter

        fetchRequests()
    }

    private fun fetchRequests() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.w("RequestHistoryFragment", "User not logged in")
            return
        }

        db.collection("requests")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                requestList.clear()
                for (document in documents) {
                    val request = document.toObject(Request::class.java).apply { id = document.id }
                    requestList.add(request)
                }
                adapter.notifyDataSetChanged()
                displayRequestCount(requestList.size)
            }
            .addOnFailureListener { e ->
                Log.w("RequestHistoryFragment", "Error fetching requests", e)
            }
    }

    private fun displayRequestCount(count: Int) {
        binding.receivedRequestCount.text = "Total Requests: $count"
    }

    private fun onEditClick(request: Request) {
        val intent = Intent(requireContext(), CreateRequestActivity::class.java)
        intent.putExtra("REQUEST_ID", request.id)
        startActivity(intent)
    }

    private fun onDeleteClick(request: Request) {
        db.collection("requests").document(request.id)
            .update("isDeleted", true) // Mark as deleted
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Request deleted successfully", Toast.LENGTH_SHORT).show()
                fetchRequests() // Refresh the list
            }
            .addOnFailureListener { e ->
                Log.w("RequestHistoryFragment", "Error deleting request", e)
                Toast.makeText(requireContext(), "Error deleting request", Toast.LENGTH_SHORT).show()
            }
    }
}