package com.welfare.blood.donation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.databinding.ActivityAllDonorsBinding

class AllDonorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllDonorsBinding
    private lateinit var db: FirebaseFirestore

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
    }

    private fun fetchDonors(requestId: String) {
        db.collection("requests").document(requestId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val donors = document.get("donors") as? List<String> ?: emptyList()
                    displayDonors(donors)
                } else {
                    Log.e("AllDonorsActivity", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.e("AllDonorsActivity", "Error fetching document", e)
            }
    }

    private fun displayDonors(donors: List<String>) {
        binding.donorsList.text = donors.joinToString("\n") { it }
    }
}
