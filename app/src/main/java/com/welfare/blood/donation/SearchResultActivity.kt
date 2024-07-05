package com.welfare.blood.donation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.databinding.ActivitySearchResultBinding

class SearchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        db = FirebaseFirestore.getInstance()

        userAdapter = UserAdapter(this, emptyList())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchResultActivity)
            adapter = userAdapter
        }

        val searchId = intent.getStringExtra("searchId")
        if (searchId != null) {
            fetchUsers(searchId)
        } else {
            Log.e(TAG, "No searchId found in Intent extras")
            Toast.makeText(this, "No searchId found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUsers(searchId: String) {
        db.collection("searches").document(searchId).get()
            .addOnSuccessListener { document ->
                val bloodGroup = document.getString("bloodGroup")
                val city = document.getString("city")

                if (bloodGroup != null && city != null) {
                    db.collection("users")
                        .whereEqualTo("bloodGroup", bloodGroup)
                        .whereEqualTo("city", city)
                        .get()
                        .addOnSuccessListener { result ->
                            val users = result.toObjects(User::class.java)
                            if (users.isNotEmpty()) {
                                userAdapter.updateData(users)
                            } else {
                                Toast.makeText(this, "No users found matching criteria", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error fetching users", e)
                            Toast.makeText(this, "Error fetching users: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Invalid search data", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Invalid search data: bloodGroup=$bloodGroup, city=$city")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching search document", e)
                Toast.makeText(this, "Error fetching search document: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TAG = "SearchResultActivity"
    }
}
