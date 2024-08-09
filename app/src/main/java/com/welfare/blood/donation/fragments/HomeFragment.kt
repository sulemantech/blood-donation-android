package com.welfare.blood.donation.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.welfare.blood.donation.BloodbankActivity
import com.welfare.blood.donation.BothHistoryActivity
import com.welfare.blood.donation.CreateRequestActivity
import com.welfare.blood.donation.CriticalPatientsListActivity
import com.welfare.blood.donation.DonateBloodActivity
import com.welfare.blood.donation.ProfileActivity
import com.welfare.blood.donation.R
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

        // Set the title for this fragment
        activity?.title = getString(R.string.title_home)

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
                        val welcomeText = "Welcome, $userName"
                        tvWelcomeMessage.text = createSpannableText(welcomeText)
                    } else {
                        tvWelcomeMessage.text = createSpannableText("Welcome")
                    }
                }
                .addOnFailureListener {
                    tvWelcomeMessage.text = createSpannableText("Welcome")
                }
        }

        // Setup RecyclerView
        binding.criticalPatientRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        criticalPatientAdapter = CriticalPatientAdapter(criticalPatients)
        binding.criticalPatientRecyclerview.adapter = criticalPatientAdapter

        fetchCriticalPatients()

        binding.frameLayoutOne.setOnClickListener {
            startActivity(Intent(requireContext(), BothHistoryActivity::class.java))
        }

        binding.frameLayoutTwo.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

        binding.frameLayoutThree.setOnClickListener {
            startActivity(Intent(requireContext(), DonateBloodActivity::class.java))
        }

        binding.frameLayoutFour.setOnClickListener {
            startActivity(Intent(requireContext(), BloodbankActivity::class.java))
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
        binding.criticalPatientRecyclerview.setOnClickListener {
            startActivity(Intent(requireContext(), CriticalPatientsListActivity::class.java))
        }

        binding.criticalPatientRecyclerview.setOnClickListener {
            startActivity(Intent(requireContext(), CriticalPatientsListActivity::class.java))
        }
    }

    // Function to create SpannableString with outer stroke effect
    private fun createSpannableText(text: String): SpannableString {
        val spannableString = SpannableString(text)

        // Apply stroke (background) color
        spannableString.setSpan(
            ForegroundColorSpan(Color.WHITE), // Stroke color
            0, text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Apply text color over the stroke
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#B20B1F")),
            0, text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

    private fun fetchCriticalPatients() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        db.collection("requests")
            .whereEqualTo("critical", true)
            .whereNotEqualTo("userId", currentUserId)
            .orderBy("dateRequired", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("HomeFragment", "Error fetching critical patients", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    criticalPatients.clear()
                    for (document in snapshots) {
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
                    showCriticalPatient(criticalPatients)
                }
            }
    }

    private fun showCriticalPatient(criticalPatients: MutableList<CriticalPatient>) {


    }


    private fun displayPatientCount(count: Int) {
        binding.emergencyPatientsLabel.text = "Emergency Patients: $count"
    }
}
