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
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.welfare.blood.donation.AddDonorsActivity
import com.welfare.blood.donation.BloodbankActivity
import com.welfare.blood.donation.BothHistoryActivity
import com.welfare.blood.donation.CriticalPatientsListActivity
import com.welfare.blood.donation.DonateBloodActivity
import com.welfare.blood.donation.ProfileActivity
import com.welfare.blood.donation.R
import com.welfare.blood.donation.SearchActivity
import com.welfare.blood.donation.adapters.CriticalPatientAdapter
import com.welfare.blood.donation.databinding.FragmentHome2Binding
import com.welfare.blood.donation.models.CriticalPatient
import com.welfare.blood.donation.models.Request
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHome2Binding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var tvWelcomeMessage: TextView
    private lateinit var criticalPatientAdapter: CriticalPatientAdapter
    private val criticalPatients = mutableListOf<CriticalPatient>()
    private lateinit var addDonorsButton: FrameLayout // Reference to the button
    private var currentUserId: String? = null // Store currentUserId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHome2Binding.inflate(inflater, container, false)
        tvWelcomeMessage = binding.home1 // Initialize here
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = getString(R.string.title_home)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        addDonorsButton = binding.btnFive

        currentUserId = auth.currentUser?.uid

        currentUserId?.let { uid ->
            db.collection("users").document(uid).addSnapshotListener { documentSnapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                documentSnapshot?.let { doc ->
                    val userName = doc.getString("name")
                    val userType = doc.getString("userType")
                    val welcomeText = "Welcome, $userName"
                    tvWelcomeMessage.text = createSpannableText(welcomeText)

                    if (userType == "admin") {
                        addDonorsButton.visibility = View.VISIBLE
                        addDonorsButton.setOnClickListener {
                            startActivity(Intent(requireContext(), AddDonorsActivity::class.java))
                        }
                    } else {
                        addDonorsButton.visibility = View.GONE
                    }
                } ?: run {
                    tvWelcomeMessage.text = createSpannableText("Welcome")
                }
            }
        }

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
    }

    private fun createSpannableText(text: String): SpannableString {
        val spannableString = SpannableString(text)

        spannableString.setSpan(
            ForegroundColorSpan(Color.WHITE),
            0, text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#B20B1F")),
            0, text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

    private fun fetchCriticalPatients() {
        currentUserId?.let { uid ->
            db.collection("requests")
                .whereEqualTo("critical", true)
                .whereNotEqualTo("userId", uid)
                .orderBy("dateRequired", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "Error fetching critical patients", e)
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
                        displayPatientCount()
                    }
                }
        }
    }

    private suspend fun getCriticalRequestCount(): Int {
        return try {
            val querySnapshot = db.collection("requests")
                .whereEqualTo("critical", true)
                .whereNotEqualTo("userId", currentUserId)
                .get()
                .await()
            querySnapshot.size()
        } catch (e: Exception) {
            Log.w(TAG, "Error getting critical request count", e)

        }
    }

    private fun displayPatientCount() {
        lifecycleScope.launch {
            val count = getCriticalRequestCount()
            binding.emergencyPatientsLabel.text = "Emergency Patients: $count"
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
