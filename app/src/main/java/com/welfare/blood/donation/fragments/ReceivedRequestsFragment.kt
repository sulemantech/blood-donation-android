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
import com.welfare.blood.donation.RequestAdapter
import com.welfare.blood.donation.databinding.FragmentReceivedRequestsBinding
import com.welfare.blood.donation.models.Request

class ReceivedRequestsFragment : Fragment() {

    private lateinit var binding: FragmentReceivedRequestsBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var requestList: MutableList<Request>
    private lateinit var adapter: RequestAdapter
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
        adapter = RequestAdapter(requestList)
        binding.recyclerView.adapter = adapter

        fetchRequests()
        binding.createRequest.setOnClickListener {
            val intent = Intent(requireContext(), CreateRequestActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchRequests() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.w("ReceivedRequestsFragment", "User not logged in")
            return
        }

        db.collection("requests")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                requestList.clear()
                for (document in documents) {
                    val request = document.toObject(Request::class.java)
                    requestList.add(request)
                }
                adapter.notifyDataSetChanged()
                displayRequestCount(requestList.size)
            }
            .addOnFailureListener { e ->
                Log.w("ReceivedRequestsFragment", "Error fetching requests", e)
            }
    }
        private fun displayRequestCount(count: Int) {
            binding.receivedRequestCount.text = "Requests Received: $count"
        }
    }

