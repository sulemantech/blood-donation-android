package com.welfare.blood.donation.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.BloodbankActivity
import com.welfare.blood.donation.BothHistoryActivity
import com.welfare.blood.donation.CreateRequestActivity
import com.welfare.blood.donation.CriticalPatientsListActivity
import com.welfare.blood.donation.DonateBloodActivity
import com.welfare.blood.donation.ProfileActivity
import com.welfare.blood.donation.SearchActivity
import com.welfare.blood.donation.adapters.CriticalPatientAdapter
import com.welfare.blood.donation.databinding.FragmentHome2Binding
import com.welfare.blood.donation.models.CriticalPatient
import com.welfare.blood.donation.models.Request

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHome2Binding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var tvWelcomeMessage: TextView
    private lateinit var criticalPatientAdapter: CriticalPatientAdapter
    private val criticalPatients = mutableListOf<CriticalPatient>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHome2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        tvWelcomeMessage = binding.home1

        // Fetch user details from Firestore
        val userID = auth.currentUser?.uid
        userID?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("name")
                        tvWelcomeMessage.text = "Welcome, $userName"
                    } else {
                        tvWelcomeMessage.text = "Welcome"
                    }
                }
                .addOnFailureListener {
                    tvWelcomeMessage.text = "Welcome"
                }
        }

        // Setup RecyclerView
        binding.criticalPatientRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        criticalPatientAdapter = CriticalPatientAdapter(criticalPatients)
        binding.criticalPatientRecyclerview.adapter = criticalPatientAdapter

        fetchCriticalPatients()

        binding.cardview1.setOnClickListener {
            startActivity(Intent(requireContext(), CreateRequestActivity::class.java))
        }

        binding.cardview2.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

        binding.cardview3.setOnClickListener {
            startActivity(Intent(requireContext(), BloodbankActivity::class.java))
        }

        binding.cardview4.setOnClickListener {
            startActivity(Intent(requireContext(), DonateBloodActivity::class.java))
        }

        binding.cardview5.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

        binding.cardview6.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }
        binding.tvSeeAll.setOnClickListener {
            startActivity(Intent(requireContext(), CriticalPatientsListActivity::class.java))
        }
    }

    private fun fetchCriticalPatients() {
        db.collection("requests")
            .whereEqualTo("critical", true)
            .limit(1)
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
                        critical = request.critical // This will now work
                    )
                    criticalPatients.add(criticalPatient)
                }
                criticalPatientAdapter.notifyDataSetChanged()
                displayPatientCount(criticalPatients.size)
            }
            .addOnFailureListener { e ->
                Log.w("HomeFragment", "Error fetching critical patients", e)
            }
    }

    private fun displayPatientCount(count: Int) {
        binding.emergencyPatientsLabel.text = "Emergency Patients: $count"
    }

}
