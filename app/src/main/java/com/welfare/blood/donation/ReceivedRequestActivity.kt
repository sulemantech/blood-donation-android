package com.welfare.blood.donation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.databinding.ActivityReceivedRequestBinding
import com.welfare.blood.donation.models.Request

class ReceivedRequestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceivedRequestBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var requestList: MutableList<Request>
    private lateinit var adapter: RequestAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceivedRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        requestList = mutableListOf()
        adapter = RequestAdapter(requestList)
        binding.recyclerView.adapter = adapter

        fetchRequests()
        binding.createRequest.setOnClickListener {
            createRequestForMyself()
        }
    }

    private fun fetchRequests() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.w("ReceivedRequestsActivity", "User not logged in")
            return
        }

        db.collection("requests")
            .get()
            .addOnSuccessListener { documents ->
                requestList.clear()
                for (document in documents) {
                    val request = document.toObject(Request::class.java)
                    if (request.userId != currentUser.uid) {
                        requestList.add(request)
                    }
                }
                adapter.notifyDataSetChanged()
                displayRequestCount(requestList.size)
            }
            .addOnFailureListener { e ->
                Log.w("ReceivedRequestsActivity", "Error fetching requests", e)
            }
    }

    private fun displayRequestCount(count: Int) {
        binding.receivedRequestCount.text = "Requests Received: $count"
    }

    private fun createRequestForMyself() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val name = document.getString("name") ?: ""
                    val phone = document.getString("phone") ?: ""
                    val bloodGroup = document.getString("bloodGroup") ?: ""
                    val location = document.getString("location") ?: ""
                    val email = document.getString("email") ?: ""

                    val intent = Intent(this, CreateRequestActivity::class.java).apply {
                        putExtra("bloodFor", "Myself")
                        putExtra("name", name)
                        putExtra("phone", phone)
                        putExtra("bloodGroup", bloodGroup)
                        putExtra("location", location)
                        putExtra("email", email)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
            }
    }
}
