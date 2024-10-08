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
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.databinding.ActivityCreateRequestBinding
import com.welfare.blood.donation.models.Request
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CreateRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateRequestBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var selectedDonationDate: String
    private var isCritical: Boolean = false
    private var notified: Boolean = false
    private lateinit var bloodGroups: Array<String>
    private lateinit var locations: Array<String>
    private var requestId: String? = null
    private lateinit var loadingDialog:AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_loader, null)
        builder.setView(dialogView)
        builder.setCancelable(false) // Prevent dialog from being cancelled by tapping outside
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

        db = FirebaseFirestore.getInstance()

        binding.backArrow.setOnClickListener {
            navigateToHome()
        }

        binding.critical.setOnCheckedChangeListener { _, isChecked ->
            Log.d("CreateRequestActivity", "Critical status changed: $isChecked")
            isCritical = isChecked
        }

        binding.spinnerBloodGroup.setOnTouchListener { _, _ ->
            clearEditTextFocus()
            false
        }

        binding.edLocation.setOnTouchListener { _, _ ->
            clearEditTextFocus()
            false
        }
        binding.radioForMyself.isChecked = true
        fillFormFields()

        binding.bloodForMyselfGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_for_myself -> fillFormFields()
                R.id.radio_for_others -> clearFormFields()
            }
        }

        setupAutoCompleteTextViews()


        binding.sendRequest.setOnClickListener {
            if (validateInputs()) {
              //  showProgressBar()
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

        requestId = intent.getStringExtra("REQUEST_ID")
        if (requestId != null) {
            loadRequestData(requestId!!)
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


    private  fun clearEditTextFocus(){
        binding.name.clearFocus()
        binding.age.clearFocus()
        binding.requiredDate.clearFocus()
        binding.requiredUnit.clearFocus()
        binding.hospital.clearFocus()
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
                        binding.spinnerBloodGroup.setText(bloodGroup, false)
                        binding.edLocation.setText(location, false)
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
        binding.spinnerBloodGroup.setSelection(0)
        binding.edLocation.setSelection(0)
    }

    private fun getBloodTypeIndex(bloodGroup: String): Int {
        val bloodTypes = resources.getStringArray(R.array.blood_groups)
        return bloodTypes.indexOf(bloodGroup)
    }

    private fun setLocationSpinner(location: String) {
        val locations = resources.getStringArray(R.array.pakistan_cities)
        val index = locations.indexOf(location)
        if (index >= 0) {
            binding.edLocation.setSelection(index)
        }
    }

    private fun showDatePickerDialog() {
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
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                selectedDonationDate = sdf.format(calendar.time)
                binding.edDateRequired.setText(selectedDonationDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

        datePickerDialog.show()
    }

    private fun validateInputs(): Boolean {
        val patientName = binding.patientName.text.toString().trim()
        val ageStr = binding.age.text.toString().trim()
        val bloodType = binding.spinnerBloodGroup.text.toString().trim()
        val requiredUnitStr = binding.requiredUnit.text.toString().trim()
        val dateRequired = binding.edDateRequired.text.toString().trim()
      //  val phone = binding.edDateRequired.text.toString().trim()
        val location = binding.edLocation.text.toString().trim()

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
//        if (phone.isEmpty()) {
//            binding.phone.error = "Phone Number is required"
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

        Log.d("CreateRequestActivity", "Critical status before sending request: $isCritical")

        val selectedId = binding.bloodForMyselfGroup.checkedRadioButtonId
        val bloodFor = findViewById<RadioButton>(selectedId).text.toString()

        val request = hashMapOf(
            "patientName" to binding.patientName.text.toString().trim(),
            "age" to binding.age.text.toString().trim().toInt(),
            "bloodType" to binding.spinnerBloodGroup.text.toString().trim(),
            "requiredUnit" to binding.requiredUnit.text.toString().trim().toInt(),
            "dateRequired" to binding.edDateRequired.text.toString().trim(),
            "hospital" to binding.hospital.text.toString().trim(),
            "location" to binding.edLocation.text.toString().trim(),
            "bloodFor" to bloodFor,
            "userId" to currentUser.uid,
            "recipientId" to currentUser.uid,
            "status" to "pending",
            "critical" to isCritical,
            "notified" to notified,
        )

        loadingDialog.show()
        db.collection("requests")
            .add(request)
            .addOnSuccessListener {
                loadingDialog.dismiss()
              //  Toast.makeText(this, "Request sent successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SentSuccessfullActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                loadingDialog.dismiss()
                Log.w("CreateRequestActivity", "Error adding request", e)
             //   Toast.makeText(this, "Error sending request", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateRequest() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("CreateRequestActivity", "Critical status before updating request: $isCritical")

        val selectedId = binding.bloodForMyselfGroup.checkedRadioButtonId
        val bloodFor = findViewById<RadioButton>(selectedId).text.toString()

        val updatedRequest = mapOf(
            "patientName" to binding.patientName.text.toString().trim(),
            "age" to binding.age.text.toString().trim().toInt(),
            "bloodType" to binding.spinnerBloodGroup.text.toString().trim(),
            "requiredUnit" to binding.requiredUnit.text.toString().trim().toInt(),
            "dateRequired" to binding.edDateRequired.text.toString().trim(),
            "hospital" to binding.hospital.text.toString().trim(),
            "location" to binding.edLocation.text.toString().trim(),
            "bloodFor" to bloodFor,
            "userId" to currentUser.uid,
            "recipientId" to currentUser.uid,
            "status" to "pending",
            "critical" to isCritical
        )

        loadingDialog.show()
        requestId?.let {
            db.collection("requests").document(it)
                .update(updatedRequest)
                .addOnSuccessListener {

                    loadingDialog.dismiss()
                  //  Toast.makeText(this, "Request updated successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, BothHistoryActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    loadingDialog.show()
                    Log.w("CreateRequestActivity", "Error updating request", e)
                    //Toast.makeText(this, "Error updating request", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadRequestData(requestId: String) {
        db.collection("requests").document(requestId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val request = document.toObject(Request::class.java)
                    if (request != null) {
                        binding.patientName.setText(request.patientName)
                     //   binding.phone.setText(request.phone)
                        binding.age.setText(request.age.toString())
                        binding.spinnerBloodGroup.setText(request.bloodType)
                        binding.requiredUnit.setText(request.requiredUnit.toString())
                        binding.edDateRequired.setText(request.dateRequired)
                        binding.hospital.setText(request.hospital)
                        binding.edLocation.setText(request.location)
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

    override fun onBackPressed() {
        navigateToHome()
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
