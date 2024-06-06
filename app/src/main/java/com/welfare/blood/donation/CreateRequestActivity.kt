package com.welfare.blood.donation

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.welfare.blood.donation.databinding.ActivityCreateRequestBinding
import java.util.*

class CreateRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.backArrow.setOnClickListener {
            onBackPressed()
        }
        binding.edDateRequired.setOnClickListener {
            showDatePickerDialog(binding.edDateRequired)
        }

        binding.sendRequest.setOnClickListener {
            sendRequest()
        }

    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "${selectedDay.toString().padStart(2, '0')}/" +
                        "${(selectedMonth + 1).toString().padStart(2, '0')}/" +
                        selectedYear
                editText.setText(selectedDate)
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun sendRequest() {
        val patientName = binding.edName.text.toString()
        val bloodType = binding.spinner.selectedItem.toString()
        val requiredUnits = binding.spinner2.selectedItem.toString()
        val requiredDate = binding.edDateRequired.text.toString()
        val requesterType = when (binding.radioGroup.checkedRadioButtonId) {
            R.id.radio1 -> "Myself"
            R.id.radio2 -> "For other"
            else -> ""
        }

        if (patientName.isEmpty() || requiredDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Proceed with sending the request and navigating to the next activity
        // For demonstration purposes, let's assume we're navigating to SuccessfulActivity
        val intent = Intent(this, SentSuccessfullActivity::class.java)
        startActivity(intent)


    // Send the request (network request or database operation can be done here)
        // Show success message or handle the result accordingly
    }
}
