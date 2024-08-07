package com.welfare.blood.donation

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.databinding.ActivityCreateRequestBinding
import com.welfare.blood.donation.models.Request
import java.text.SimpleDateFormat
import java.util.*

class CreateRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateRequestBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var selectedDonationDate: String
    private var isCritical: Boolean = false
    private var requestId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        binding.backArrow.setOnClickListener {
            navigateToHome()
        }

        binding.critical.setOnCheckedChangeListener { _, isChecked ->
            isCritical = isChecked
        }

        // Check the "Myself" radio button and fill fields if checked
        binding.radioForMyself.isChecked = true
        fillFormFields()

        binding.bloodForMyselfGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_for_myself -> fillFormFields()
                R.id.radio_for_others -> clearFormFields()
            }
        }

        binding.sendRequest.setOnClickListener {
            if (validateInputs()) {
                showProgressBar()
                if (requestId != null) {
                    updateRequest()
                } else {
                    sendRequest()
                }
            }
        }

        binding.edDateRequired.setOnClickListener {
            showDatePickerDialog()
        }

        // Check if it's an edit request
        requestId = intent.getStringExtra("REQUEST_ID")
        if (requestId != null) {
            loadRequestData(requestId!!)
        }
    }

    private fun fillFormFields() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name") ?: ""
                        val bloodGroup = document.getString("bloodGroup") ?: ""
                        val location = document.getString("location") ?: ""

                        binding.patientName.setText(name)
                        binding.bloodType.setSelection(getBloodTypeIndex(bloodGroup))
                        setLocationSpinner(location)
                    } else {
                        Log.d("CreateRequestActivity", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("CreateRequestActivity", "get failed with ", exception)
                }
        }
    }

    private fun clearFormFields() {
        binding.patientName.setText("")
        binding.bloodType.setSelection(0)
        binding.location.setSelection(0)
    }

    private fun getBloodTypeIndex(bloodGroup: String): Int {
        val bloodTypes = resources.getStringArray(R.array.blood_groups)
        return bloodTypes.indexOf(bloodGroup)
    }

    private fun setLocationSpinner(location: String) {
        val locations = resources.getStringArray(R.array.pakistan_cities)
        val index = locations.indexOf(location)
        if (index >= 0) {
            binding.location.setSelection(index)
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            calendar.set(selectedYear, selectedMonth, selectedDay)
            selectedDonationDate = sdf.format(calendar.time)
            binding.edDateRequired.setText(selectedDonationDate)
        }, year, month, day).show()
    }

    private fun validateInputs(): Boolean {
        val patientName = binding.patientName.text.toString().trim()
        val ageStr = binding.age.text.toString().trim()
        val bloodType = binding.bloodType.selectedItem.toString().trim()
        val requiredUnitStr = binding.requiredUnit.text.toString().trim()
        val dateRequired = binding.edDateRequired.text.toString().trim()
        val location = binding.location.selectedItem.toString().trim()

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

        return true
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.sendRequest.visibility = View.GONE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        binding.sendRequest.visibility = View.VISIBLE
    }

    private fun sendRequest() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedId = binding.bloodForMyselfGroup.checkedRadioButtonId
        val bloodFor = findViewById<RadioButton>(selectedId).text.toString()

        val request = hashMapOf(
            "patientName" to binding.patientName.text.toString().trim(),
            "age" to binding.age.text.toString().trim().toInt(),
            "bloodType" to binding.bloodType.selectedItem.toString().trim(),
            "requiredUnit" to binding.requiredUnit.text.toString().trim().toInt(),
            "dateRequired" to binding.edDateRequired.text.toString().trim(),
            "hospital" to binding.hospital.text.toString().trim(),
            "location" to binding.location.selectedItem.toString().trim(),
            "bloodFor" to bloodFor,
            "userId" to currentUser.uid,
            "recipientId" to currentUser.uid,
            "status" to "pending",
            "critical" to isCritical
        )

        db.collection("requests")
            .add(request)
            .addOnSuccessListener {
                hideProgressBar()
                Toast.makeText(this, "Request sent successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SentSuccessfullActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                hideProgressBar()
                Log.w("CreateRequestActivity", "Error adding request", e)
                Toast.makeText(this, "Error sending request", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadRequestData(requestId: String) {
        db.collection("requests").document(requestId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val request = document.toObject(Request::class.java)
                    if (request != null) {
                        binding.patientName.setText(request.patientName)
                        binding.age.setText(request.age.toString())
                        binding.bloodType.setSelection(getBloodTypeIndex(request.bloodType))
                        binding.requiredUnit.setText(request.requiredUnit.toString())
                        binding.edDateRequired.setText(request.dateRequired)
                        binding.hospital.setText(request.hospital)
                        setLocationSpinner(request.location)
                        binding.critical.isChecked = request.critical
                        isCritical = request.critical
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("CreateRequestActivity", "Error loading request data", e)
                Toast.makeText(this, "Error loading request data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateRequest() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedId = binding.bloodForMyselfGroup.checkedRadioButtonId
        val bloodFor = findViewById<RadioButton>(selectedId).text.toString()

        val updatedRequest = mapOf(
            "patientName" to binding.patientName.text.toString().trim(),
            "age" to binding.age.text.toString().trim().toInt(),
            "bloodType" to binding.bloodType.selectedItem.toString().trim(),
            "requiredUnit" to binding.requiredUnit.text.toString().trim().toInt(),
            "dateRequired" to binding.edDateRequired.text.toString().trim(),
            "hospital" to binding.hospital.text.toString().trim(),
            "location" to binding.location.selectedItem.toString().trim(),
            "bloodFor" to bloodFor,
            "userId" to currentUser.uid,
            "recipientId" to currentUser.uid,
            "status" to "pending",
            "critical" to isCritical
        )

        requestId?.let {
            db.collection("requests").document(it)
                .update(updatedRequest)
                .addOnSuccessListener {
                    hideProgressBar()
                    Toast.makeText(this, "Request updated successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    hideProgressBar()
                    Log.w("CreateRequestActivity", "Error updating request", e)
                    Toast.makeText(this, "Error updating request", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onBackPressed() {
        navigateToHome()
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
