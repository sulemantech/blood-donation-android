package com.welfare.blood.donation

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.welfare.blood.donation.databinding.ActivityRegisterBinding
import com.welfare.blood.donation.models.CommunityDonors
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var bloodGroups: Array<String>
    private lateinit var locations: Array<String>
    private lateinit var selectedDateOfBirth: String
    private lateinit var selectedLastDonationDate: String
    private var isAddedByAdmin: Boolean = false
    private var userId: String? = null
    private var isPasswordVisible = false
    private lateinit var loadingDialog: AlertDialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAutoCompleteTextViews()

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_loader, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        loadingDialog = builder.create()

        binding.imgTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        val checkBox = findViewById<CheckBox>(R.id.agree)
        val termsText = "I confirm I am of legal age and agree \nto the  the Terms and Data Privacy"
        val spannableString = SpannableString(termsText)

        val termsStart = termsText.indexOf("Terms")
        val termsEnd = termsStart + "Terms".length

        val privacyStart = termsText.indexOf("Data Privacy")
        val privacyEnd = privacyStart + "Data Privacy".length

        val termsClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@RegisterActivity, TermConditionActivity::class.java)
                startActivity(intent)
            }
        }

        val privacyClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@RegisterActivity, PrivacyPolicyActivity::class.java)
                startActivity(intent)
            }
        }

        spannableString.setSpan(termsClickableSpan, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(privacyClickableSpan, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val termsColorSpan = ForegroundColorSpan(Color.BLUE)
        val privacyColorSpan = ForegroundColorSpan(Color.BLUE)

        spannableString.setSpan(termsColorSpan, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(privacyColorSpan, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        checkBox?.text = spannableString
        checkBox?.movementMethod = LinkMovementMethod.getInstance()


        val mTextView = findViewById<TextView>(R.id.register)
        val mString1 = "Sign In"
        val mSpannableString = SpannableString(mString1)
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
        mTextView.text = mSpannableString


        binding.register.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
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
            binding.imgTogglePassword.visibility =View.GONE
            binding.haveAccount.visibility=View.GONE
            binding.register.visibility=View.GONE
            binding.noYes.isChecked = true
            binding.agree.isChecked = true
        } else {
            binding.bloodDonation.text = "Sign Up"
            binding.bloodDonation2.text="Please enter your details"

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

    private fun setupAutoCompleteTextViews() {
        bloodGroups = resources.getStringArray(R.array.blood_groups)
        locations = resources.getStringArray(R.array.pakistan_cities)

        // Setup Blood Group AutoCompleteTextView
        val bloodGroupAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bloodGroups)
        binding.spinnerBloodGroup.setAdapter(bloodGroupAdapter)
        binding.spinnerBloodGroup.setThreshold(1) // Show suggestions after 1 character

        // Setup Location AutoCompleteTextView
        val locationAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, locations)
        binding.edLocation.setAdapter(locationAdapter)
        binding.edLocation.setThreshold(1) // Show suggestions after 1 character

        // Add TextWatcher to validate input
        binding.spinnerBloodGroup.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateBloodGroup(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateLocation(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Clear focus on item selected
        (binding.spinnerBloodGroup as AutoCompleteTextView).setOnItemClickListener { _, _, _, _ ->
            binding.spinnerBloodGroup.clearFocus()
        }

        (binding.edLocation as AutoCompleteTextView).setOnItemClickListener { _, _, _, _ ->
            binding.edLocation.clearFocus()
        }
    }

    private fun validateBloodGroup(input: String) {
        if (!bloodGroups.contains(input)) {
            binding.spinnerBloodGroup.error = "Invalid blood group"
        } else {
            binding.spinnerBloodGroup.error = null
        }
    }

    private fun validateLocation(input: String) {
        if (!locations.contains(input)) {
            binding.edLocation.error = "Invalid location"
        } else {
            binding.edLocation.error = null
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

                      //  setupSpinners()

                        binding.spinnerBloodGroup.setText(communityDonors.bloodGroup)
                        binding.edLocation.setText(communityDonors.location)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("RegisterActivity", "Error loading request data", e)
                Toast.makeText(this, "Error loading request data", Toast.LENGTH_SHORT).show()
            }
    }

//    private fun setupSpinners() {
//        val bloodGroups = resources.getStringArray(R.array.blood_groups)
//        val bloodGroupAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroups)
//        bloodGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        val adapter = ArrayAdapter(this, R.layout.spinner_item, R.id.spinner_item_text, bloodGroups)
//        binding.spinnerBloodGroup.adapter = bloodGroupAdapter
//        binding.spinnerBloodGroup.adapter = adapter
//
//    }

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
        val bloodGroup = binding.spinnerBloodGroup.text.toString()
        val wantsToDonate = binding.noYes.isChecked
        val location = binding.edLocation.text.toString()
        val userType = "user"

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || bloodGroup.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

       // binding.progressBar.visibility = View.VISIBLE

        FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
            if (tokenTask.isSuccessful) {
                val fcmToken = tokenTask.result

                if (userId != null) {
                    updateExistingUser(
                        email,
                        name,
                        phone,
                        bloodGroup,
                        location,
                        wantsToDonate,
                        userType,
                        fcmToken
                    )
                } else {
                    val userID = db.collection("users").document().id

                    val user = hashMapOf(
                        "userID" to userID,
                        "name" to name,
                        "email" to email,
                        "phone" to phone,
                        "bloodGroup" to bloodGroup,
                        "location" to location,
                        "isDonor" to wantsToDonate,
                        "fcmToken" to fcmToken,
                        "lastLoginAt" to null,
                        "userType" to userType,
                        "addedByAdmin" to true,
                        "registrationTimestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                    )

                    loadingDialog.show() //
                    db.collection("users").document(userID).set(user, SetOptions.merge())
                        .addOnSuccessListener {
                            loadingDialog.dismiss()
                          //  Log.d(TAG, "DocumentSnapshot successfully written!")
                          //  binding.progressBar.visibility = View.GONE
                           // Toast.makeText(this, "New user Added", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, AddDonorsActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        .addOnFailureListener { e ->
                            loadingDialog.dismiss()
                          //  Log.w(TAG, "Error writing document", e)
                            binding.progressBar.visibility = View.GONE
                          //  Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Log.w(TAG, "Fetching FCM token failed", tokenTask.exception)
              //  binding.progressBar.visibility = View.GONE
                loadingDialog.dismiss()
              //  Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
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
        userType: String,
        fcmToken: String?
    ) {
        val userUpdates = hashMapOf(
            "email" to email,
            "name" to name,
            "phone" to phone,
            "bloodGroup" to bloodGroup,
            "location" to location,
            "isDonor" to wantsToDonate,
            "userType" to userType,
            "addedByAdmin" to true,
            "fcmToken" to fcmToken
        )

        loadingDialog.show() // Show the loading dialog

        db.collection("users").document(userId!!)
            .set(userUpdates, SetOptions.merge())
            .addOnSuccessListener {
                loadingDialog.dismiss() // Show the loading dialog

                //  Log.d(TAG, "User details successfully updated!")
               /// binding.progressBar.visibility = View.GONE
               // Toast.makeText(this, "User details updated", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, AddDonorsActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                loadingDialog.dismiss() // Show the loading dialog

                // Log.w(TAG, "Error updating document", e)
               // binding.progressBar.visibility = View.GONE
              //  Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
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
        val bloodGroup = binding.spinnerBloodGroup.text.toString()
        val location = binding.edLocation.text.toString()
        val password = binding.edPassword.text.toString().trim()

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }
        if (!isValidPhone(phone)) {
            Toast.makeText(this, "Please enter a valid contact number", Toast.LENGTH_SHORT).show()
            return
        }

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || bloodGroup.isEmpty() || location.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

       // binding.progressBar.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userID = auth.currentUser?.uid

                    if (userID != null) {
                        FirebaseMessaging.getInstance().token
                            .addOnCompleteListener { tokenTask ->
                                if (tokenTask.isSuccessful) {
                                    val fcmToken = tokenTask.result

                                    val user = hashMapOf(
                                        "userID" to userID,
                                        "name" to name,
                                        "email" to email,
                                        "phone" to phone,
                                        "bloodGroup" to bloodGroup,
                                        "location" to location,
                                        "isDonor" to binding.noYes.isChecked,
                                        "fcmToken" to fcmToken,
                                        "lastLoginAt" to null,
                                        "userType" to "user",
                                        "addedByAdmin" to false,
                                        "registrationTimestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                                    )

                                    loadingDialog.show()

                                    db.collection("users").document(userID).set(user, SetOptions.merge())
                                        .addOnSuccessListener {
                                            loadingDialog.dismiss()

                                            Log.d(TAG, "DocumentSnapshot successfully written!")
                                          //  binding.progressBar.visibility = View.GONE
                                           // Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()

                                            val intent = Intent(this, HomeActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        .addOnFailureListener { e ->
                                            loadingDialog.dismiss()

                                            Log.w(TAG, "Error writing document", e)
                                           // binding.progressBar.visibility = View.GONE
                                         //   Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Log.w(TAG, "Fetching FCM token failed", tokenTask.exception)
                                    //binding.progressBar.visibility = View.GONE
                                    loadingDialog.dismiss() // Show the loading dialog

                                    //
                              //  Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                  //  binding.progressBar.visibility = View.GONE
                   // Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
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
