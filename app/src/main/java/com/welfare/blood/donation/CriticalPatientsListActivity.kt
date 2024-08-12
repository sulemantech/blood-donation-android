package com.welfare.blood.donation

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
import com.welfare.blood.donation.adapters.CriticalPatientAdapter
import com.welfare.blood.donation.databinding.ActivityCriticalPatientsListBinding
import com.welfare.blood.donation.models.CriticalPatient
import com.welfare.blood.donation.models.Request
import java.text.SimpleDateFormat
import java.util.*

class CriticalPatientsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriticalPatientsListBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var criticalPatientAdapter: CriticalPatientAdapter
    private val criticalPatients = mutableListOf<CriticalPatient>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriticalPatientsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar()

        db = FirebaseFirestore.getInstance()

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        setupRecyclerView()
        fetchAllCriticalPatients()
    }

    private fun configureStatusBar() {
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
    }

    private fun setupRecyclerView() {
        binding.criticalPatientRecyclerview.layoutManager = LinearLayoutManager(this)
        criticalPatientAdapter = CriticalPatientAdapter(criticalPatients, object : CriticalPatientAdapter.OnPatientClickListener {
            override fun onPatientClick(patient: CriticalPatient) {
                // Handle patient click
                // Example: navigate to a detailed view
            }
        })
        binding.criticalPatientRecyclerview.adapter = criticalPatientAdapter
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

    private fun fetchAllCriticalPatients() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        db.collection("requests")
            .whereEqualTo("critical", true)
            .get()
            .addOnSuccessListener { documents ->
                criticalPatients.clear()
                for (document in documents) {
                    val userId = document.getString("userId") ?: ""
                    if (userId == currentUserId) continue // Skip current user's requests

                    val requiredUnitField = document.get("requiredUnit")
                    Log.d("FirestoreDataType", "requiredUnit type: ${requiredUnitField?.javaClass?.name}")

                    val requiredUnit = when (requiredUnitField) {
                        is Number -> requiredUnitField.toInt()
                        is String -> requiredUnitField.toIntOrNull() ?: 0
                        else -> 0
                    }

                    val request = Request(
                        patientName = document.getString("patientName") ?: "",
                        age = document.getLong("age")?.toInt() ?: 0,
                        bloodType = document.getString("bloodType") ?: "",
                        requiredUnit = requiredUnit,
                        dateRequired = document.getString("dateRequired") ?: "",
                        hospital = document.getString("hospital") ?: "",
                        location = document.getString("location") ?: "",
                        bloodFor = document.getString("bloodFor") ?: "",
                        userId = userId,
                        recipientId = document.getString("recipientId") ?: "",
                        status = document.getString("status") ?: "",
                        critical = document.getBoolean("critical") ?: false
                    )

                    val criticalPatient = CriticalPatient(
                        patientName = request.patientName,
                        age = request.age,
                        bloodType = request.bloodType,
                        requiredUnit = request.requiredUnit,
                        dateRequired = request.dateRequired,
                        hospital = request.hospital,
                        location = request.location,
                        bloodFor = request.bloodFor,
                        userId = request.userId,
                        recipientId = request.recipientId,
                        status = request.status,
                        critical = request.critical
                    )
                    criticalPatients.add(criticalPatient)
                }

                // Sort the criticalPatients list by dateRequired in descending order
                try {
                    criticalPatients.sortByDescending {
                        dateFormat.parse(it.dateRequired)
                    }
                } catch (e: Exception) {
                    Log.e("CriticalPatientsListActivity", "Error parsing date", e)
                }

                criticalPatientAdapter.notifyDataSetChanged()
                displayPatientCount(criticalPatients.size)
            }
            .addOnFailureListener { e ->
                Log.w("CriticalPatientsListActivity", "Error fetching critical patients", e)
                displayError("Error fetching data. Please try again later.")
            }
    }

    private fun displayPatientCount(count: Int) {
        binding.emergencyPatientsLabel.text = "Critical Patients: $count"
    }

    private fun displayError(message: String) {
        // Display an error message to the user, for example with a Toast
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
