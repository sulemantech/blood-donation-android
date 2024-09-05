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
import java.text.SimpleDateFormat
import java.util.*

class CriticalPatientsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriticalPatientsListBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var criticalPatientAdapter: CriticalPatientAdapter
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
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        }
    }

    private fun setupRecyclerView() {
        binding.criticalPatientRecyclerview.layoutManager = LinearLayoutManager(this)
        criticalPatientAdapter = CriticalPatientAdapter(mutableListOf(), object : CriticalPatientAdapter.OnPatientClickListener {
            override fun onPatientClick(patient: CriticalPatient) {
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
        db.collection("requests")
            .whereEqualTo("critical", true)
            .get()
            .addOnSuccessListener { documents ->
                val patients = mutableListOf<CriticalPatient>()
                for (document in documents) {
                    val patient = document.toObject(CriticalPatient::class.java)
                    patient.id = document.id
                    if (patient.userId != currentUserId) {
                        patients.add(patient)
                    }
                }

                // Sort the patients list by dateRequired
                try {
                    patients.sortByDescending {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.dateRequired)
                    }
                } catch (e: Exception) {
                    Log.e("CriticalPatientsListActivity", "Error parsing date", e)
                }

                criticalPatientAdapter.updatePatients(patients)
                displayPatientCount(patients.size)
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreError", "Error fetching documents", e)
                displayError("Error fetching data. Please try again later.")
            }
    }

    private fun displayPatientCount(count: Int) {
        binding.emergencyPatientsLabel.text = "Critical Patients: $count"
    }

    private fun displayError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
