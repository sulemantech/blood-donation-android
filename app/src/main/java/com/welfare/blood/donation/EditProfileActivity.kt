package com.welfare.blood.donation

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.welfare.blood.donation.databinding.ActivityEditProfileBinding
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var selectedDateOfBirth: String
    private lateinit var selectedLastDonationDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserProfile()

        binding.edDateBirth.setOnClickListener {
            showDatePickerDialogForDateOfBirth()
        }

        binding.edLastdonationdate.setOnClickListener {
            showDatePickerDialogForLastDonationDate()
        }

        binding.btnSave.setOnClickListener {
            saveUserProfile()
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser ?: return
        val userId = user.uid

        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document != null) {
                binding.edName.setText(document.getString("name"))
                binding.edEmail.setText(document.getString("email"))
                binding.edPhone.setText(document.getString("phone"))
                binding.edCity.setText(document.getString("city"))
                binding.edLocation.setText(document.getString("location"))
                binding.edDateBirth.setText(document.getString("dateOfBirth"))
                binding.edLastdonationdate.setText(document.getString("lastDonationDate"))
                binding.noYes.isChecked = document.getBoolean("isDonor") == true
                // Set spinner value for blood group
                val bloodGroup = document.getString("bloodType")
                val bloodGroups = resources.getStringArray(R.array.blood_groups)
                val index = bloodGroups.indexOf(bloodGroup)
                binding.spinnerBloodGroup.setSelection(index)
            }
        }
    }

    private fun showDatePickerDialogForDateOfBirth() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            calendar.set(selectedYear, selectedMonth, selectedDay)
            selectedDateOfBirth = sdf.format(calendar.time)
            binding.edDateBirth.setText(selectedDateOfBirth)
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun showDatePickerDialogForLastDonationDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            calendar.set(selectedYear, selectedMonth, selectedDay)
            selectedLastDonationDate = sdf.format(calendar.time)
            binding.edLastdonationdate.setText(selectedLastDonationDate)
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun saveUserProfile() {
        val user = auth.currentUser ?: return
        val userId = user.uid

        val name = binding.edName.text.toString().trim()
        val email = binding.edEmail.text.toString().trim()
        val phone = binding.edPhone.text.toString().trim()
        val dateOfBirth = binding.edDateBirth.text.toString().trim()
        val bloodGroup = binding.spinnerBloodGroup.selectedItem.toString()
        val city = binding.edCity.text.toString().trim()
        val location = binding.edLocation.text.toString().trim()
        val lastDonationDate = binding.edLastdonationdate.text.toString().trim()
        val isDonor = binding.noYes.isChecked

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || dateOfBirth.isEmpty() || bloodGroup.isEmpty() || city.isEmpty() || location.isEmpty() || lastDonationDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        val userMap = hashMapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "dateOfBirth" to dateOfBirth,
            "bloodType" to bloodGroup,
            "city" to city,
            "location" to location,
            "lastDonationDate" to lastDonationDate,
            "isDonor" to isDonor
        )

        db.collection("users").document(userId).set(userMap, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Profile Update Failed", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TAG = "EditProfileActivity"
    }
}
