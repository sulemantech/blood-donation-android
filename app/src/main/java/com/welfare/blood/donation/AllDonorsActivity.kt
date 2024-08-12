package com.welfare.blood.donation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.adapters.DonorAdapter
import com.welfare.blood.donation.databinding.ActivityAllDonorsBinding
import com.welfare.blood.donation.models.User

class AllDonorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllDonorsBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var donorAdapter: DonorAdapter
    private val donorsList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllDonorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        db = FirebaseFirestore.getInstance()

        binding.donorRecyclerView.layoutManager = LinearLayoutManager(this)
        donorAdapter = DonorAdapter(donorsList)
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
                  //  Toast.makeText(this@AllDonorsActivity, "Donor IDs: $donorIds", Toast.LENGTH_SHORT).show()
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
            db.collection("users")
                .whereIn("userID", donorIds)
                .get()
                .addOnSuccessListener { documents ->
                    donorsList.clear()
                    for (document in documents) {
                        val donor = document.toObject(User::class.java)
                       // Toast.makeText(this@AllDonorsActivity, "Donor: $donor", Toast.LENGTH_SHORT).show()
                        donorsList.add(donor)
                    }
                    donorAdapter.notifyDataSetChanged()
                    displayDonorCount(donorsList.size)
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
