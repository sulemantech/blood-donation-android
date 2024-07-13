package com.welfare.blood.donation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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
                val location = document.getString("location") // Changed from city to location

                if (location != null) {
                    Log.d(TAG, "Search criteria: bloodGroup=$bloodGroup, location=$location") // Changed from city to location
                    var usersRef = db.collection("users").whereEqualTo("location", location.trim()) // Changed from city to location

                    // Add bloodGroup filter if bloodGroup is not null
                    if (!bloodGroup.isNullOrEmpty()) {
                        usersRef = usersRef.whereEqualTo("bloodGroup", bloodGroup.trim())
                    }

                    usersRef.get()
                        .addOnSuccessListener { result ->
                            handleUserResult(result)
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error fetching users", e)
                            Toast.makeText(this, "Error fetching users: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Invalid search data", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Invalid search data: bloodGroup=$bloodGroup, location=$location") // Changed from city to location
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching search document", e)
                Toast.makeText(this, "Error fetching search document: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun handleUserResult(result: QuerySnapshot) {
        val users = result.toObjects(User::class.java)
        if (users.isNotEmpty()) {
            userAdapter.updateData(users)
            Log.d(TAG, "Users found: ${users.size}")
        } else {
            Log.d(TAG, "No users found matching criteria")
            Toast.makeText(this, "No users found matching criteria", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "SearchResultActivity"
    }
}
