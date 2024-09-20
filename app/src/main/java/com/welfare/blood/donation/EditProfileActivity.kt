package com.welfare.blood.donation

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.welfare.blood.donation.databinding.ActivityEditProfileBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private lateinit var bloodGroups: Array<String>
    private lateinit var locations: Array<String>
    private lateinit var selectedDateOfBirth: String
    private lateinit var selectedLastDonationDate: String
    private var userProfileImageUrl: String? = null
    private var selectedGender: String? = null
    private lateinit var loadingDialog:AlertDialog

    companion object {
        private const val TAG = "EditProfileActivity"
        private const val IMAGE_PICK_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_loader, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        loadingDialog = builder.create()

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
        storageRef = FirebaseStorage.getInstance().reference

        setupAutoCompleteTextViews()

        loadUserProfile()

        binding.edDateBirth.setOnClickListener { showDatePickerDialogForDateOfBirth() }
        binding.edLastdonationdate.setOnClickListener { showDatePickerDialogForLastDonationDate() }

        binding.profileImageView.setOnClickListener { pickImageFromGallery() }

        binding.btnSave.setOnClickListener { saveUserProfile() }
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

    private fun setupAutoCompleteTextViews() {
        bloodGroups = resources.getStringArray(R.array.blood_groups)
        locations = resources.getStringArray(R.array.pakistan_cities)

        val bloodGroupAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bloodGroups)
        binding.spinnerBloodGroup.setAdapter(bloodGroupAdapter)
        binding.spinnerBloodGroup.setThreshold(1)

        val locationAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, locations)
        binding.edLocation.setAdapter(locationAdapter)
        binding.edLocation.setThreshold(1)

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

    private fun loadUserProfile() {
        val user = auth.currentUser ?: return
        val userId = user.uid

        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document != null) {
                binding.edName.setText(document.getString("name"))
                binding.edEmail.setText(document.getString("email"))
                binding.edPhone.setText(document.getString("phone"))
                binding.edDateBirth.setText(document.getString("dateOfBirth"))
                binding.edLastdonationdate.setText(document.getString("lastDonationDate"))
                binding.noYes.isChecked = document.getBoolean("isDonor") == true

                // Set the text for AutoCompleteTextViews
                binding.spinnerBloodGroup.setText(document.getString("bloodGroup"))
                binding.edLocation.setText(document.getString("location"))

                val gender = document.getString("gender")
                val spinnerPosition = (binding.spinnerGender.adapter as ArrayAdapter<String>).getPosition(gender)
                binding.spinnerGender.setSelection(spinnerPosition)


                val imageUrl = document.getString("profileImageUrl")
                if (!imageUrl.isNullOrEmpty()) {
                    userProfileImageUrl = imageUrl
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.baseline_person_outline_24)
                        .error(R.drawable.baseline_person_outline_24)
                        .into(binding.profileImageView)
                }
            }
        }
    }

    private fun saveUserProfile() {
        val user = auth.currentUser ?: return
        val userId = user.uid

        val bloodGroup = binding.spinnerBloodGroup.text.toString()
        val location = binding.edLocation.text.toString()
        val gender = binding.spinnerGender.selectedItem.toString()

        if (!bloodGroups.contains(bloodGroup)) {
            showToast("Invalid blood group. Please select from the list.")
            return
        }

        if (!locations.contains(location)) {
            showToast("Invalid location. Please select from the list.")
            return
        }

        val userProfile = hashMapOf(
            "name" to binding.edName.text.toString(),
            "email" to binding.edEmail.text.toString(),
            "phone" to binding.edPhone.text.toString(),
            "dateOfBirth" to binding.edDateBirth.text.toString(),
            "lastDonationDate" to binding.edLastdonationdate.text.toString(),
            "isDonor" to binding.noYes.isChecked,
            "bloodGroup" to bloodGroup,
            "location" to location,
            "gender" to gender,
            "profileImageUrl" to userProfileImageUrl
        )

        loadingDialog.show()
        db.collection("users").document(userId).set(userProfile, SetOptions.merge())
            .addOnSuccessListener {
                loadingDialog.dismiss()
                setResult(RESULT_OK)
                finish()
               // showToast("Profile updated successfully!")
            }
            .addOnFailureListener {
                loadingDialog.dismiss() // Hide the loading dialog
              //  showToast("Error updating profile.")
            }
    }

    private fun showDatePickerDialogForDateOfBirth() {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        if (::selectedDateOfBirth.isInitialized) {
            try {
                val date = sdf.parse(selectedDateOfBirth)
                date?.let {
                    calendar.time = it
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        } else {
            calendar.timeInMillis = System.currentTimeMillis()
        }

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                calendar.set(selectedYear, selectedMonth, selectedDay)
                selectedDateOfBirth = sdf.format(calendar.time)
                binding.edDateBirth.setText(selectedDateOfBirth)
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.minDate = Calendar.getInstance().apply {
            add(Calendar.YEAR, -100)
        }.timeInMillis

        datePickerDialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        datePickerDialog.show()
    }

    private fun showDatePickerDialogForLastDonationDate() {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        if (::selectedLastDonationDate.isInitialized) {
            try {
                val date = sdf.parse(selectedLastDonationDate)
                date?.let {
                    calendar.time = it
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        } else {
            calendar.timeInMillis = System.currentTimeMillis()
        }

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                selectedLastDonationDate = sdf.format(calendar.time)
                binding.edLastdonationdate.setText(selectedLastDonationDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            imageUri?.let {
                uploadImageToFirebaseStorage(it)
            }
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val profileImageRef = storageRef.child("profile_images/$userId.jpg")

        profileImageRef.putFile(imageUri)
            .addOnSuccessListener {
                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    userProfileImageUrl = uri.toString()
                    Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.baseline_person_outline_24)
                        .error(R.drawable.baseline_person_outline_24)
                        .into(binding.profileImageView)
                }
            }
            .addOnFailureListener {
                showToast("Failed to upload image.")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
