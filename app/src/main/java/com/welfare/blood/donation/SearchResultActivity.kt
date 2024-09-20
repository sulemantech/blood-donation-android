package com.welfare.blood.donation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.welfare.blood.donation.databinding.ActivitySearchResultBinding

class SearchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var userAdapter: UserAdapter

    private var bloodGroup: String? = null
    private var location: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adjust window flags for transparent status bar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        }

        // Set back arrow functionality
        binding.backArrow.setOnClickListener {
            navigateBackToSearch()
        }

        db = FirebaseFirestore.getInstance()

        userAdapter = UserAdapter(this, emptyList())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchResultActivity)
            adapter = userAdapter
        }

        // Retrieve blood group and location from intent
        bloodGroup = intent.getStringExtra("bloodGroup")
        location = intent.getStringExtra("location")

        if (bloodGroup != null && location != null) {
            fetchUsers(bloodGroup!!, location!!)
        } else {
            Log.e(TAG, "Search criteria not found in Intent extras")
            Toast.makeText(this, "No search criteria found", Toast.LENGTH_SHORT).show()
            navigateBackToSearch()
        }
    }

    // Custom method to handle navigating back to SearchActivity with search criteria
    private fun navigateBackToSearch() {
        val intent = Intent(this, SearchActivity::class.java)
        intent.putExtra("bloodGroup", bloodGroup)
        intent.putExtra("location", location)
        startActivity(intent)
        finish() // Ensure this activity is finished after navigation
    }

    // Override the back press to retain search criteria
    override fun onBackPressed() {
        navigateBackToSearch()
    }

    // Method to set window flags
    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    // Fetch users based on search criteria
    private fun fetchUsers(bloodGroup: String, location: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE

        Log.d(TAG, "Search criteria: bloodGroup=$bloodGroup, location=$location")

        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid

        var usersRef = db.collection("users")
            .whereEqualTo("location", location.trim())

        if (bloodGroup.isNotEmpty()) {
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
            navigateBackToSearch()
        }

        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }

    companion object {
        private const val TAG = "SearchResultActivity"
    }
}
