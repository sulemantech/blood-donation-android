package com.welfare.blood.donation

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.welfare.blood.donation.databinding.ActivityRegisterBinding
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog

    private lateinit var selectedDateOfBirth: String
    private lateinit var selectedLastDonationDate: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering...")
        progressDialog.setCancelable(false)

        binding.signUp.setOnClickListener {
            registerUser()
        }

        binding.edDateBirth.setOnClickListener {
            showDatePickerDialogForDateOfBirth()
        }

        binding.edLastdonationdate.setOnClickListener {
            showDatePickerDialogForLastDonationDate()
        }
    }

    private fun showDatePickerDialogForDateOfBirth() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
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

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            calendar.set(selectedYear, selectedMonth, selectedDay)
            selectedLastDonationDate = sdf.format(calendar.time)
            binding.edLastdonationdate.setText(selectedLastDonationDate)
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            binding.edLastdonationdate.setText("$selectedYear-${selectedMonth + 1}-$selectedDay")
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun registerUser() {
        val name = binding.edName.text.toString().trim()
        val email = binding.edEmail.text.toString().trim()
        val phone = binding.edPhone.text.toString().trim()
        val dateOfBirth = binding.edDateBirth.text.toString().trim()
        val bloodGroup = binding.spinnerBloodGroup.selectedItem.toString()
        val wantsToDonate = binding.noYes.isChecked
        val city = binding.edCity.text.toString().trim()
        val location = binding.edLocation.text.toString().trim()
        val lastDonationDate = binding.edLastdonationdate.text.toString().trim()
        val password = binding.edPassword.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || dateOfBirth.isEmpty() || bloodGroup.isEmpty() || city.isEmpty() || location.isEmpty() || lastDonationDate.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        progressDialog.show()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressDialog.dismiss()

                if (task.isSuccessful) {
                    val userID = auth.currentUser!!.uid

                    val user = hashMapOf(
                        "userID" to userID,
                        "name" to name,
                        "email" to email,
                        "phone" to phone,
                        "bloodType" to bloodGroup,
                        "city" to city,
                        "location" to location,
                        "lastDonationDate" to lastDonationDate,
                        "isDonor" to wantsToDonate,
                        "lastLoginAt" to null,
                        "dateOfBirth" to dateOfBirth
                    )

                    db.collection("users").document(userID).set(user, SetOptions.merge())
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully written!")
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error writing document", e)
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}//after complete the register than i want to create a user profile and user will edit their profile also



//import android.content.Intent
//import android.os.Bundle
//import android.util.Patterns
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.UserProfileChangeRequest
//import com.welfare.blood.donation.databinding.ActivityRegisterBinding
//
//class RegisterActivity : AppCompatActivity() {
//
//    private lateinit var auth: FirebaseAuth
//    private lateinit var binding: ActivityRegisterBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityRegisterBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        auth = FirebaseAuth.getInstance()
//
//        binding.signUp.setOnClickListener {
//            registerUser()
//        }
//    }
//
//    private fun registerUser() {
//        val nameStr = binding.edName.text.toString().trim()
//        val phoneStr = binding.edPhone.text.toString().trim()
//        val emailStr = binding.edEmail.text.toString().trim()
//        val passwordStr = binding.edPassword.text.toString().trim()
//        val dobStr = binding.edDateBirth.text.toString().trim()
//        val bloodGroupStr = binding.spinnerBloodGroup.selectedItem.toString()
//        val donateBloodStr = binding.noYes.isChecked
//
//        if (nameStr.isEmpty()) {
//            binding.edName.error = "Name is required"
//            binding.edName.requestFocus()
//            return
//        }
//
//        if (phoneStr.isEmpty()) {
//            binding.edPhone.error = "Phone number is required"
//            binding.edPhone.requestFocus()
//            return
//        }
//
//        if (emailStr.isEmpty()) {
//            binding.edEmail.error = "Email is required"
//            binding.edEmail.requestFocus()
//            return
//        }
//
//        if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
//            binding.edEmail.error = "Please provide valid email"
//            binding.edEmail.requestFocus()
//            return
//        }
//
//        if (passwordStr.isEmpty()) {
//            binding.edPassword.error = "Password is required"
//            binding.edPassword.requestFocus()
//            return
//        }
//
//        if (passwordStr.length < 6) {
//            binding.edPassword.error = "Minimum password length should be 6 characters!"
//            binding.edPassword.requestFocus()
//            return
//        }
//
//        binding.progressBar.visibility = View.VISIBLE
//
//        auth.createUserWithEmailAndPassword(emailStr, passwordStr)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val user = auth.currentUser
//                    val profileUpdates = UserProfileChangeRequest.Builder()
//                        .setDisplayName(nameStr)
//                        .build()
//                    user?.updateProfile(profileUpdates)
//                        ?.addOnCompleteListener { updateTask ->
//                            if (updateTask.isSuccessful) {
//                                Toast.makeText(this, "User registered successfully", Toast.LENGTH_LONG).show()
//                                binding.progressBar.visibility = View.GONE
//                                val intent = Intent(this,LoginActivity::class.java)
//                                startActivity(intent)
//                            } else {
//                                Toast.makeText(this, "Registration failed: ${updateTask.exception?.message}", Toast.LENGTH_LONG).show()
//                                binding.progressBar.visibility = View.GONE
//                            }
//                        }
//                } else {
//                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
//                    binding.progressBar.visibility = View.GONE
//                }
//            }
//    }
//}

//
//        binding.signUp.setOnClickListener {
//            val enteredUsername = binding.edName.text.toString()
//            val enteredPassword = binding.edPassword.text.toString()
//            val enteredEmail = binding.edEmail.text.toString()
//            val enteredPhone = binding.edPhone.text.toString()
//            val enteredDateBirth = binding.edDateBirth.text.toString()
//            val donateBlood = binding.noYes.isChecked
//
//            //validations
//            if (enteredUsername.isEmpty() || enteredPassword.isEmpty() || enteredEmail.isEmpty() || enteredPhone.isEmpty() || enteredDateBirth.isEmpty()) {
//                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
//            } else if (enteredUsername.length < 6) {
//                Toast.makeText(this, "Username must be at least 6 characters long", Toast.LENGTH_SHORT).show()
//            } else if (enteredPassword.length < 8) {
//                Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
//            } else if (!containsSpecialCharacter(enteredPassword)) {
//                Toast.makeText(this, "Password must contain at least one special character", Toast.LENGTH_SHORT).show()
//            } else if (!isValidEmail(enteredEmail)) {
//                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
//            } else if (!isValidPhoneNumber(enteredPhone)) {
//                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
//            } else if (!isValidDateOfBirth(enteredDateBirth)) {
//                Toast.makeText(this, "Please enter a valid date of birth (yyyy-MM-dd)", Toast.LENGTH_SHORT).show()
//            } else {
//                try {
//                    val phoneNumber = enteredPhone.toLong()
//                    registerUser(enteredUsername, phoneNumber, enteredEmail, enteredPassword, enteredDateBirth, "O+", donateBlood)
//                } catch (e: NumberFormatException) {
//                    Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//    private fun registerUser(name: String, phoneNumber: Long, email: String, password: String, dateOfBirth: String, bloodGroup: String, donateBlood: Boolean) {
//        val registerRequest = RegisterRequest(
//            name,
//            phoneNumber,
//            email,
//            password,
//            dateOfBirth,
//            bloodGroup,
//            donateBlood
//        )
//
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = RetrofitInstance.api.registerUser(registerRequest)
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@RegisterActivity, "Registration Successful: ${response.name}", Toast.LENGTH_LONG).show()
//                    val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
//                    startActivity(intent)
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@RegisterActivity, "Registration Failed: ${e.message}", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }
//
//    private fun isValidEmail(email: String): Boolean {
//        val pattern = Pattern.compile("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}\$")
//        val matcher = pattern.matcher(email)
//        return matcher.matches()
//    }
//
//    private fun isValidPhoneNumber(phone: String): Boolean {
//        val pattern = Pattern.compile("^\\d{11}\$")
//        val matcher = pattern.matcher(phone)
//        return matcher.matches()
//    }
//
//    private fun isValidDateOfBirth(dateOfBirth: String): Boolean {
//        val pattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}\$")
//        val matcher = pattern.matcher(dateOfBirth)
//        return matcher.matches()
//    }
//
//    private fun containsSpecialCharacter(text: String): Boolean {
//        val pattern = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]")
//        val matcher = pattern.matcher(text)
//        return matcher.find()

