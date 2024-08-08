package com.welfare.blood.donation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.adapters.DonorAdapter
import com.welfare.blood.donation.databinding.ActivityAllDonorsBinding
import com.welfare.blood.donation.models.Request

class AllDonorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllDonorsBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var donorAdapter: DonorAdapter
    private val donorRequestList = mutableListOf<Request>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllDonorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        db = FirebaseFirestore.getInstance()

        // Initialize RecyclerView and Adapter
        binding.donorRecyclerView.layoutManager = LinearLayoutManager(this)
        donorAdapter = DonorAdapter(donorRequestList)
        binding.donorRecyclerView.adapter = donorAdapter

        val requestId = intent.getStringExtra("REQUEST_ID")
        Log.d("AllDonorsActivity", "REQUEST_ID: $requestId")

        if (requestId != null) {
            fetchDonorRequestDetails(requestId)
        } else {
            Log.e("AllDonorsActivity", "No request ID found in intent")
        }
    }

    private fun fetchDonorRequestDetails(requestId: String) {
        db.collection("requests").document(requestId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val donorIds = document.get("donors") as? List<String> ?: emptyList()
                    Log.d("AllDonorsActivity", "Donor IDs: $donorIds")
                    fetchRequestsByDonorIds(donorIds)
                } else {
                    Log.e("AllDonorsActivity", "No such document in 'requests' collection")
                }
            }
            .addOnFailureListener { e ->
                Log.e("AllDonorsActivity", "Error fetching document", e)
            }
    }

    private fun fetchRequestsByDonorIds(donorIds: List<String>) {
        if (donorIds.isNotEmpty()) {
            db.collection("requests")
                .whereIn("userId", donorIds)
                .get()
                .addOnSuccessListener { documents ->
                    donorRequestList.clear()
                    for (document in documents) {
                        val request = document.toObject(Request::class.java)
                        Log.d("AllDonorsActivity", "Fetched Request: $request")
                        donorRequestList.add(request)
                    }
                    donorAdapter.notifyDataSetChanged()
                    displayDonorCount(donorRequestList.size)
                }
                .addOnFailureListener { e ->
                    Log.e("AllDonorsActivity", "Error fetching requests", e)
                }
        } else {
            Log.e("AllDonorsActivity", "No donors found in request")
        }
    }

    private fun displayDonorCount(count: Int) {
        binding.donorListCount.text = "Donors: $count"
        Log.d("AllDonorsActivity", "Donor Count: $count")
    }
}
