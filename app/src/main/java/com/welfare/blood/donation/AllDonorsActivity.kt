package com.welfare.blood.donation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.adapters.DonorsAdapter
import com.welfare.blood.donation.databinding.ActivityAllDonorsBinding
import com.welfare.blood.donation.models.Donors

class AllDonorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllDonorsBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: DonorsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllDonorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        val requestId = intent.getStringExtra("REQUEST_ID")
        if (requestId != null) {
            fetchDonors(requestId)
        } else {
            Log.e("AllDonorsActivity", "No request ID found in intent")
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.donorsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DonorsAdapter(emptyList())
        binding.donorsRecyclerView.adapter = adapter
    }

    private fun fetchDonors(requestId: String) {
        db.collection("requests").document(requestId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val donorIds = document.get("donors") as? List<String> ?: emptyList()
                    if (donorIds.isNotEmpty()) {
                        fetchDonorDetails(donorIds)
                    } else {
                        Log.e("AllDonorsActivity", "No donors found for this request")
                    }
                } else {
                    Log.e("AllDonorsActivity", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.e("AllDonorsActivity", "Error fetching document", e)
            }
    }

    private fun fetchDonorDetails(donorIds: List<String>) {
        db.collection("users")
            .whereIn("userId", donorIds)
            .get()
            .addOnSuccessListener { documents ->
                val donors = mutableListOf<Donors>()
                for (document in documents) {
                    val donor = document.toObject(Donors::class.java)
                    donors.add(donor)
                }
                displayDonors(donors)
            }
            .addOnFailureListener { e ->
                Log.e("AllDonorsActivity", "Error fetching donor details", e)
            }
    }

    private fun displayDonors(donors: List<Donors>) {
        adapter = DonorsAdapter(donors)
        binding.donorsRecyclerView.adapter = adapter
        binding.donorListCount.text = "Donors: ${donors.size}"
    }
}
