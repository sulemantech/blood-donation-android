package com.welfare.blood.donation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
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
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE

        db.collection("searches").document(searchId).get()
            .addOnSuccessListener { document ->
                val bloodGroup = document.getString("bloodGroup")
                val location = document.getString("location")

                if (location != null) {
                    Log.d(TAG, "Search criteria: bloodGroup=$bloodGroup, location=$location")

                    val currentUserID = FirebaseAuth.getInstance().currentUser?.uid

                    var usersRef = db.collection("users")
                        .whereEqualTo("location", location.trim())

                    if (!bloodGroup.isNullOrEmpty()) {
                        usersRef = usersRef.whereEqualTo("bloodGroup", bloodGroup.trim())
                    }

                    usersRef.get()
                        .addOnSuccessListener { result ->
                            handleUserResult(result, currentUserID)
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error fetching users", e)
                            Toast.makeText(this, "Error fetching users: ${e.message}", Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                        }
                } else {
                    Toast.makeText(this, "Invalid search data", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Invalid search data: bloodGroup=$bloodGroup, location=$location")
                    binding.progressBar.visibility = View.GONE
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching search document", e)
                Toast.makeText(this, "Error fetching search document: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
    }
    private fun handleUserResult(result: QuerySnapshot, currentUserID: String?) {
        val users = result.toObjects(Register::class.java)
        val filteredUsers = users.filter { user -> user.userID != currentUserID }

        if (filteredUsers.isNotEmpty()) {
            userAdapter.updateData(filteredUsers)
            Log.d(TAG, "Users found: ${filteredUsers.size}")
        } else {
            Log.d(TAG, "No users found matching criteria")
            Toast.makeText(this, "No users found matching criteria", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }
    companion object {
        private const val TAG = "SearchResultActivity"
    }
}
