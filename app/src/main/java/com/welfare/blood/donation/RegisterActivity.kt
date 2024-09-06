package com.welfare.blood.donation

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.welfare.blood.donation.databinding.ActivityRegisterBinding
import com.welfare.blood.donation.models.CommunityDonors
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var selectedDateOfBirth: String
    private lateinit var selectedLastDonationDate: String
    private var isAddedByAdmin: Boolean = false
    private var userId: String? = null
    private var isPasswordVisible = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }


        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        }
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        userId = intent.getStringExtra("USER_ID")
        isAddedByAdmin = intent.getBooleanExtra("isAddedByAdmin", false)

        if (isAddedByAdmin) {
            binding.bloodDonation.text = "Community Donor"
            binding.bloodDonation2.text="Please enter your details for Community Donor"
            binding.edPassword.visibility = View.GONE
            binding.noYes.isChecked = true
            binding.agree.isChecked = true
        } else {
            binding.bloodDonation.text = "Sign Up"
            binding.bloodDonation2.text="Please enter your details to Sign up"

        }

        if (userId != null) {
            loadRegisterData(userId!!)
            binding.edPassword.visibility = View.GONE
        }

        binding.progressBar.visibility = View.GONE

        binding.signUp.setOnClickListener {
            if (binding.agree.isChecked && binding.noYes.isChecked) {
                if (isAddedByAdmin) {
                    upsertUserByAdmin()
                } else {
                    registerUser()
                }
            } else {
                Toast.makeText(this, "Please agree to the Terms and Conditions and confirm you are ready to donate", Toast.LENGTH_SHORT).show()
            }
        }

        binding.edLastdonationdate.setOnClickListener {
            showDatePickerDialogForLastDonationDate()
        }

        binding.term.setOnClickListener {
            val intent = Intent(this, TermConditionActivity::class.java)
            startActivity(intent)
        }

        binding.data.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        binding.spinnerBloodGroup.setOnTouchListener { _, _ ->
            clearEditTextFocus()
            false
        }

        binding.edLocation.setOnTouchListener { _, _ ->
            clearEditTextFocus()
            false
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {

            binding.edPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.imgTogglePassword.setImageResource(R.drawable.ic_vector_eye)
        } else {

            binding.edPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.imgTogglePassword.setImageResource(R.drawable.baseline_remove_red_eye_24)
        }


        binding.edPassword.setSelection(binding.edPassword.text?.length ?: 0)

        isPasswordVisible = !isPasswordVisible
    }

    private  fun clearEditTextFocus(){
        binding.edName.clearFocus()
        binding.edPhone.clearFocus()
        binding.edPassword.clearFocus()
        binding.edEmail.clearFocus()
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    private fun loadRegisterData(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val communityDonors = document.toObject(CommunityDonors::class.java)
                    if (communityDonors != null) {
                        binding.edName.setText(communityDonors.name)
                        binding.edPhone.setText(communityDonors.phone)
                        binding.edEmail.setText(communityDonors.email)

                        setupSpinners()

                        binding.spinnerBloodGroup.setSelection(getBloodTypeIndex(communityDonors.bloodGroup))
                        binding.edLocation.setSelection(getLocationIndex(communityDonors.location))
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("RegisterActivity", "Error loading request data", e)
                Toast.makeText(this, "Error loading request data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSpinners() {
        val bloodGroups = resources.getStringArray(R.array.blood_groups)
        val bloodGroupAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroups)
        bloodGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, R.id.spinner_item_text, bloodGroups)
        binding.spinnerBloodGroup.adapter = bloodGroupAdapter
        binding.spinnerBloodGroup.adapter = adapter

    }

    private fun getBloodTypeIndex(bloodType: String): Int {
        val adapter = binding.spinnerBloodGroup.adapter
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).toString().equals(bloodType, ignoreCase = true)) {
                return i
            }
        }
        return -1
    }

    private fun getLocationIndex(location: String): Int {
        val adapter = binding.edLocation.adapter
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).toString().equals(location, ignoreCase = true)) {
                return i
            }
        }
        return -1
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        )
        return emailPattern.matcher(email).matches()
    }

    private fun upsertUserByAdmin() {
        val name = binding.edName.text.toString().trim()
        val email = binding.edEmail.text.toString().trim()
        val phone = binding.edPhone.text.toString().trim()
        val bloodGroup = binding.spinnerBloodGroup.selectedItem.toString()
        val wantsToDonate = binding.noYes.isChecked
        val location = binding.edLocation.selectedItem.toString()
        val userType = "user"

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || bloodGroup.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId != null) {
            updateExistingUser(
                email,
                name,
                phone,
                bloodGroup,
                location,
                wantsToDonate,
                userType
            )
        } else {
            binding.progressBar.visibility = View.VISIBLE

            val userID = db.collection("users").document().id

            val user = hashMapOf(
                "userID" to userID,
                "name" to name,
                "email" to email,
                "phone" to phone,
                "bloodGroup" to bloodGroup,
                "location" to location,
                "isDonor" to wantsToDonate,
                "fcmToken" to null,
                "lastLoginAt" to null,
                "userType" to userType,
                "addedByAdmin" to true,
                "registrationTimestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )

            db.collection("users").document(userID).set(user, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "New user Added", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, AddDonorsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error writing document", e)
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateExistingUser(
        email: String,
        name: String,
        phone: String,
        bloodGroup: String,
        location: String,
        wantsToDonate: Boolean,
        userType: String
    ) {
        val userUpdates = hashMapOf(
            "email" to email,
            "name" to name,
            "phone" to phone,
            "bloodGroup" to bloodGroup,
            "location" to location,
            "isDonor" to wantsToDonate,
            "userType" to userType,
            "addedByAdmin" to true
        )

        db.collection("users").document(userId!!)
            .set(userUpdates, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "User details successfully updated!")
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "User details updated", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, AddDonorsActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDatePickerDialogForLastDonationDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                selectedLastDonationDate = dateFormat.format(calendar.time)
                binding.edLastdonationdate.setText(selectedLastDonationDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }


    private fun registerUser() {
        val name = binding.edName.text.toString().trim()
        val email = binding.edEmail.text.toString().trim()
        val phone = binding.edPhone.text.toString().trim()
        val bloodGroup = binding.spinnerBloodGroup.selectedItem.toString()
        val location = binding.edLocation.selectedItem.toString()
        val password = binding.edPassword.text.toString().trim()

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }
        if (!isValidPhone(phone)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || bloodGroup.isEmpty() || location.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userID = auth.currentUser?.uid

                    val user = hashMapOf(
                        "userID" to userID,
                        "name" to name,
                        "email" to email,
                        "phone" to phone,
                        "bloodGroup" to bloodGroup,
                        "location" to location,
                        "isDonor" to binding.noYes.isChecked,
                        "fcmToken" to null,
                        "lastLoginAt" to null,
                        "userType" to "user",
                        "addedByAdmin" to false,
                        "registrationTimestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                    )

                    if (userID != null) {
                        db.collection("users").document(userID).set(user, SetOptions.merge())
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully written!")
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error writing document", e)
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun isValidPhone(phone: String): Boolean {
        val phoneRegex = Regex("^(?:\\+92|0)\\d{10}$")
        val cleanedPhone = phone.replace(Regex("[\\s-]"), "")
        return phoneRegex.matches(cleanedPhone)
    }


    companion object {
        private const val TAG = "RegisterActivity"
    }
}
