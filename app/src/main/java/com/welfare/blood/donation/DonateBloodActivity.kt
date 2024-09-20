package com.welfare.blood.donation

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.databinding.ActivityDonateBloodBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DonateBloodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonateBloodBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var selectedDonationDate: String
    private lateinit var auth: FirebaseAuth
    private lateinit var bloodGroups: Array<String>
    private lateinit var locations: Array<String>
    private lateinit var loadingDialog:AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonateBloodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAutoCompleteTextViews()

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

    private fun showDatePickerDialogForLastDonationDate() {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        if (::selectedDonationDate.isInitialized) {
            try {
                val date = sdf.parse(selectedDonationDate)
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
                selectedDonationDate = sdf.format(calendar.time)
                binding.edDate.setText(selectedDonationDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

//    private fun showProgressBar() {
//        binding.progressBar.visibility = View.VISIBLE
//        binding.donateButton.visibility = View.GONE
//    }
//
//    private fun hideProgressBar() {
//        binding.progressBar.visibility = View.GONE
//        binding.donateButton.visibility = View.VISIBLE
//    }

    private fun saveDonationData() {
        val bloodType = binding.spinnerBloodGroup.text.toString()
        val location = binding.edLocation.text.toString()
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

            loadingDialog.show()
            db.collection("donations")
                .add(donationData)
                .addOnSuccessListener { documentReference ->
                    Log.d("DonateBloodActivity", "DocumentSnapshot added with ID: ${documentReference.id}")
                   // Toast.makeText(this, "Send Successful", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w("DonateBloodActivity", "Error adding document", e)
                  //  Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()                }
        } else {
            Log.w("DonateBloodActivity", "Please fill all fields")
        }
    }
}
