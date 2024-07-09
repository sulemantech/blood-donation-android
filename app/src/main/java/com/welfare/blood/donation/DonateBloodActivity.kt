package com.welfare.blood.donation

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.databinding.ActivityDonateBloodBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DonateBloodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonateBloodBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var selectedDonationDate: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonateBloodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        try {
            db = FirebaseFirestore.getInstance()
            auth = FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w("DonateBloodActivity", "Error initializing Firestore", e)
        }

        binding.donateButton.setOnClickListener {
            saveDonationData()
        }

        binding.edDate.setOnClickListener {
            showDatePickerDialogForLastDonationDate()
        }
    }

    private fun showDatePickerDialogForLastDonationDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            calendar.set(selectedYear, selectedMonth, selectedDay)
            selectedDonationDate = sdf.format(calendar.time)
            binding.edDate.setText(selectedDonationDate)
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.donateButton.visibility = View.GONE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        binding.donateButton.visibility = View.VISIBLE
    }

    private fun saveDonationData() {
        val bloodType = binding.bloodTypeEditText.selectedItem.toString()
        val location = binding.locationEditText.selectedItem.toString()
        val date = binding.edDate.text.toString()
        val currentUserId = auth.currentUser?.uid

        if (bloodType.isNotEmpty() && location.isNotEmpty() && date.isNotEmpty() && currentUserId != null) {
            val donationData = hashMapOf(
                "bloodType" to bloodType,
                "location" to location,
                "date" to date,
                "userId" to currentUserId,
                "status" to "pending"
            )

            showProgressBar()

            db.collection("donations")
                .add(donationData)
                .addOnSuccessListener { documentReference ->
                    Log.d("DonateBloodActivity", "DocumentSnapshot added with ID: ${documentReference.id}")
                    Toast.makeText(this, "Send Successful", Toast.LENGTH_SHORT).show()
                    hideProgressBar()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w("DonateBloodActivity", "Error adding document", e)
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    hideProgressBar()
                }
        } else {
            Log.w("DonateBloodActivity", "Please fill all fields")
        }
    }
}
