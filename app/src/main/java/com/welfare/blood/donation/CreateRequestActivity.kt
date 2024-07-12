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
import java.text.SimpleDateFormat
import java.util.*

class CreateRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateRequestBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var selectedDonationDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        val bloodForMyself = intent.getStringExtra("bloodFor") ?: ""
        if (bloodForMyself == "Myself") {
            binding.radioForMyself.isChecked = true
            fillFormFields()
        }

        binding.bloodForMyselfGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_for_myself) {
                fillFormFields()
            } else if (checkedId == R.id.radio_for_others) {
                clearFormFields()
            }
        }

        binding.sendRequest.setOnClickListener {
            if (validateInputs()) {
                showProgressBar()
                sendRequest()
            }
        }

        binding.edDateRequired.setOnClickListener {
            showDatePickerDialogForLastDonationDate()
        }
    }

    private fun fillFormFields() {
        binding.patientName.setText(intent.getStringExtra("name"))
        // binding.phone.setText(intent.getStringExtra("phone"))
        binding.bloodType.setSelection(getBloodTypeIndex(intent.getStringExtra("bloodGroup")!!))
        setLocationSpinner(intent.getStringExtra("location")!!)
    }

    private fun clearFormFields() {
        binding.patientName.setText("")
        // binding.phone.setText("")
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

//        if (location.isEmpty()) {
//            binding.location.error = "Location is required"
//            return false
//        }

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
            "location" to binding.location.selectedItem.toString().trim(),
            "bloodFor" to bloodFor,
            "userId" to currentUser.uid,
            "status" to "pending"
        )

        db.collection("requests")
            .add(request)
            .addOnSuccessListener { documentReference ->
                Log.d("CreateRequestActivity", "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(this, "Create Request Successfully", Toast.LENGTH_SHORT).show()
                hideProgressBar()
                navigateToOtherActivity()
            }
            .addOnFailureListener { e ->
                Log.w("CreateRequestActivity", "Error adding document", e)
                Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show()
                hideProgressBar()
            }
    }

    private fun navigateToOtherActivity() {
        val intent = Intent(this, SentSuccessfullActivity::class.java)
        startActivity(intent)
        finish()
    }
}
