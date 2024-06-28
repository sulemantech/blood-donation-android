package com.welfare.blood.donation

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.databinding.ActivityCreateRequestBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateRequestBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var selectedDonationDate: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Handle send request button click
        binding.sendRequest.setOnClickListener {
            if (validateInputs()) {
                sendRequest()
            }
        }
        binding.edDateRequired.setOnClickListener {
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
            binding.edDateRequired.setText(selectedDonationDate)
        }, year, month, day)
        datePickerDialog.show()
    }


    private fun validateInputs(): Boolean {
        val patientName = binding.patientName.text.toString().trim()
        val ageStr = binding.age.text.toString().trim()
        val bloodType = binding.bloodType.selectedItem.toString().trim()
        val requiredUnitStr = binding.requiredUnit.text.toString().trim()
        val dateRequired = binding.edDateRequired.text.toString().trim()
        val hospital = binding.edHospital.text.toString().trim()
        val location = binding.location.text.toString().trim()

        if (patientName.isEmpty()) {
            binding.patientName.error = "Patient name is required"
            return false
        }

        val age = ageStr.toIntOrNull()
        if (age == null || age <= 0) {
            binding.age.error = "Valid age is required"
            return false
        }

        val requiredUnit = requiredUnitStr.toIntOrNull()
        if (requiredUnit == null || requiredUnit <= 0) {
            binding.requiredUnit.error = "Valid required unit is required"
            return false
        }

        if (dateRequired.isEmpty()) {
            binding.edDateRequired.error = "Date required is required"
            return false
        }

        if (hospital.isEmpty()) {
            binding.edHospital.error = "Hospital is required"
            return false
        }

        if (location.isEmpty()) {
            binding.location.error = "Location is required"
            return false
        }

        return true
    }

    private fun sendRequest() {
        val selectedId = binding.bloodForMyselfGroup.checkedRadioButtonId
        val bloodFor = findViewById<RadioButton>(selectedId).text.toString()

        val user = hashMapOf(
            "patientName" to binding.patientName.text.toString().trim(),
            "age" to binding.age.text.toString().trim().toInt(),
            "bloodType" to binding.bloodType.selectedItem.toString().trim(),
            "requiredUnit" to binding.requiredUnit.text.toString().trim().toInt(),
            "dateRequired" to binding.edDateRequired.text.toString().trim(),
            "hospital" to binding.edHospital.text.toString().trim(),
            "location" to binding.location.text.toString().trim(),
            "bloodFor" to bloodFor
        )

        // Add a new document with a generated ID
        db.collection("requests")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("CreateRequestActivity", "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(this, "Create Request Successfully", Toast.LENGTH_SHORT).show()
                navigateToOtherActivity()
            }
            .addOnFailureListener { e ->
                Log.w("CreateRequestActivity", "Error adding document", e)
                Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToOtherActivity() {
        val intent = Intent(this, SentSuccessfullActivity::class.java)
        startActivity(intent)
        finish()  // Optional: call finish() if you don't want to keep CreateRequestActivity in the back stack
    }
}
