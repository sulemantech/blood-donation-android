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

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        requestList = mutableListOf()
        adapter = RequestAdapter(requestList)
        binding.recyclerView.adapter = adapter

        fetchRequests()
    }

    private fun fetchRequests() {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            db.collection("requests")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener { result ->
                    requestList.clear()
                    for (document in result) {
                        val request = document.toObject(Request::class.java)
                        requestList.add(request)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.w("RequestHistoryFragment", "Error getting documents: ", exception)
                }
        }
    }
}
