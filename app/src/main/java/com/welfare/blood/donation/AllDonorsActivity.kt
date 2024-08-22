package com.welfare.blood.donation

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.adapters.CommunityAdapter
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

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

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
