package com.welfare.blood.donation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.welfare.blood.donation.databinding.ActivityCreateRequestBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class CreateRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendRequest.setOnClickListener {
            if (validateInput()) {
                val bloodForMyself = binding.radioForMyself.isChecked
                val bloodRequest = ApiService.BloodRequest(
                    bloodForMyself = bloodForMyself,
                    patientName = binding.patientName.text.toString(),
                    age = binding.age.text.toString().toInt(),
                    bloodType = binding.bloodType.text.toString(),
                    requiredUnit = binding.requiredUnit.text.toString().toInt(),
                    requiredDate = binding.requiredDate.text.toString(),
                    hospital = binding.hospital.text.toString(),
                    location = binding.location.text.toString()
                )
                createRequest(bloodRequest)
            }
        }
    }

    private fun validateInput(): Boolean {
        val requiredUnitStr = binding.requiredUnit.text.toString()
        Log.d("CreateRequestActivity", "Required Unit Input: $requiredUnitStr")
        return when {
            binding.patientName.text.isBlank() -> {
                showToast("Please enter the patient's name")
                false
            }
            binding.age.text.isBlank() -> {
                showToast("Please enter the age")
                false
            }
            !isInteger(binding.age.text.toString()) -> {
                showToast("Please enter a valid age")
                false
            }
            binding.bloodType.text.isBlank() -> {
                showToast("Please enter the blood type")
                false
            }
            requiredUnitStr.isBlank() -> {
                showToast("Please enter the required unit")
                false
            }
            !isInteger(requiredUnitStr) -> {
                showToast("Please enter a valid required unit")
                false
            }
            requiredUnitStr.toInt() <= 0 -> {  // changed to <= 0
                showToast("Please enter a valid required unit greater than zero")
                false
            }
            binding.requiredDate.text.isBlank() -> {
                showToast("Please enter the required date")
                false
            }
            binding.hospital.text.isBlank() -> {
                showToast("Please enter the hospital name")
                false
            }
            binding.location.text.isBlank() -> {
                showToast("Please enter the location")
                false
            }
            else -> true
        }
    }
    private fun isInteger(str: String): Boolean {
        return try {
            str.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    private fun createRequest(bloodRequest: ApiService.BloodRequest) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) { RetrofitInstance.api.createRequest(bloodRequest) }
                if (response.isSuccessful) {
                    val bloodResponse = response.body()
                    showToast("Request ID: ${bloodResponse?.id}")
                    val intent = Intent(this@CreateRequestActivity, SentSuccessfullActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CreateRequestActivity", "Error: $errorBody")
                    showToast("Failed to create request: $errorBody")
                }
            } catch (e: IOException) {
                Log.e("CreateRequestActivity", "Network Error: ${e.message}")
                showToast("Network Error: ${e.message}")
            } catch (e: HttpException) {
                Log.e("CreateRequestActivity", "HTTP Error: ${e.message}")
                showToast("HTTP Error: ${e.message}")
            } catch (e: Exception) {
                Log.e("CreateRequestActivity", "Unexpected Error: ${e.message}")
                showToast("Unexpected Error: ${e.message}")
            }
        }
    }
}
