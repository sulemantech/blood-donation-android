package com.welfare.blood.donation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.adapters.CriticalPatientAdapter
import com.welfare.blood.donation.databinding.ActivityCriticalPatientsListBinding
import com.welfare.blood.donation.models.CriticalPatient
import com.welfare.blood.donation.models.Request

class CriticalPatientsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriticalPatientsListBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var criticalPatientAdapter: CriticalPatientAdapter
    private val criticalPatients = mutableListOf<CriticalPatient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriticalPatientsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        // Setup RecyclerView
        binding.criticalPatientRecyclerview.layoutManager = LinearLayoutManager(this)
        criticalPatientAdapter = CriticalPatientAdapter(criticalPatients)
        binding.criticalPatientRecyclerview.adapter = criticalPatientAdapter

        fetchAllCriticalPatients()
    }

    private fun fetchAllCriticalPatients() {
        db.collection("requests")
            .whereEqualTo("critical", true)
            .get()
            .addOnSuccessListener { documents ->
                criticalPatients.clear()
                for (document in documents) {
                    val request = document.toObject(Request::class.java)
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
                criticalPatientAdapter.notifyDataSetChanged()
                displayPatientCount(criticalPatients.size)
            }
            .addOnFailureListener { e ->
                Log.w("CriticalPatientsListActivity", "Error fetching critical patients", e)
            }
    }
    private fun displayPatientCount(count: Int) {
        binding.receivedRequestCount.text = "Critical Patients: $count"
    }
}
