package com.welfare.blood.donation

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
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
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private lateinit var selectedDateOfBirth: String
    private lateinit var selectedLastDonationDate: String
    private var userProfileImageUrl: String? = null
    private var selectedGender: String? = null

    companion object {
        private const val TAG = "EditProfileActivity"
        private const val IMAGE_PICK_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)



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

        loadUserProfile()

        binding.edDateBirth.setOnClickListener {
            showDatePickerDialogForDateOfBirth()
        }

        binding.edLastdonationdate.setOnClickListener {
            showDatePickerDialogForLastDonationDate()
        }

        binding.profileImageView.setOnClickListener {
            pickImageFromGallery()
        }

        binding.btnSave.setOnClickListener {
            saveUserProfile()
        }

        // Set up gender spinner
        val genderAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = genderAdapter

        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedGender = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
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

    private fun loadUserProfile() {
        val user = auth.currentUser ?: return
        val userId = user.uid

        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document != null) {
                binding.edName.setText(document.getString("name"))
                binding.edEmail.setText(document.getString("email"))
                binding.edPhone.setText(document.getString("phone"))
                //  binding.edCity.setText(document.getString("city"))
                // binding.edLocation.setText(document.getString("location"))
                binding.edDateBirth.setText(document.getString("dateOfBirth"))
                binding.edLastdonationdate.setText(document.getString("lastDonationDate"))
                binding.noYes.isChecked = document.getBoolean("isDonor") == true

                val bloodGroup = document.getString("bloodGroup")
                val bloodGroups = resources.getStringArray(R.array.blood_groups)
                val index = bloodGroups.indexOf(bloodGroup)
                binding.spinnerBloodGroup.setSelection(index)

                val location = document.getString("location")
                val locationArray = resources.getStringArray(R.array.pakistan_cities)
                val locationIndex = locationArray.indexOf(location)
                binding.edLocation.setSelection(locationIndex)

                val gender = document.getString("gender")
                val genderArray = resources.getStringArray(R.array.gender_array)
                val genderIndex = genderArray.indexOf(gender)
                binding.spinnerGender.setSelection(genderIndex)


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

    private fun showDatePickerDialogForDateOfBirth() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            selectedDateOfBirth = sdf.format(selectedDate.time)
            binding.edDateBirth.setText(selectedDateOfBirth)
        }, year, month, day)

        val oneYearAgo = Calendar.getInstance()
        oneYearAgo.add(Calendar.YEAR, -1)
        datePickerDialog.datePicker.minDate = oneYearAgo.timeInMillis

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

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

        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            //uploadImageToFirebase(imageUri)
        }
    }

//    private fun uploadImageToFirebase(imageUri: Uri?) {
//        if (imageUri == null) return
//
//        val user = auth.currentUser ?: return
//        val userId = user.uid
//
//        binding.progressBar.visibility = View.VISIBLE
//
//        val fileRef = storageRef.child("profile_images/$userId")
//
//        val uploadTask = fileRef.putFile(imageUri)
//        uploadTask.continueWithTask { task ->
//            if (!task.isSuccessful) {
//                task.exception?.let {
//                    throw it
//                }
//            }
//            fileRef.downloadUrl
//        }.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val downloadUri = task.result
//                userProfileImageUrl = downloadUri.toString()
//                saveUserProfile()
//            } else {
//                // Handle failures
//                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
//                binding.progressBar.visibility = View.GONE
//            }
//        }
//    }

    private fun saveUserProfile() {
        val user = auth.currentUser ?: return
        val userId = user.uid

        val name = binding.edName.text.toString().trim()
        val email = binding.edEmail.text.toString().trim()
        val phone = binding.edPhone.text.toString().trim()
        val dateOfBirth = binding.edDateBirth.text.toString().trim()
        val bloodGroup = binding.spinnerBloodGroup.selectedItem.toString()
        //  val city = binding.edCity.text.toString().trim()
        val location = binding.edLocation.selectedItem.toString().trim()
        val lastDonationDate = binding.edLastdonationdate.text.toString().trim()
        val isDonor = binding.noYes.isChecked

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || dateOfBirth.isEmpty() || bloodGroup.isEmpty() || location.isEmpty() || location.isEmpty() || lastDonationDate.isEmpty() || selectedGender.isNullOrEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        val userMap = hashMapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "dateOfBirth" to dateOfBirth,
            "bloodGroup" to bloodGroup,
            //  "city" to city,
            "location" to location,
            "lastDonationDate" to lastDonationDate,
            "isDonor" to isDonor,
            "gender" to selectedGender,
            "profileImageUrl" to userProfileImageUrl
        )

        db.collection("users").document(userId).set(userMap, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Profile Update Failed", Toast.LENGTH_SHORT).show()
            }
    }
}
