package com.welfare.blood.donation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.welfare.blood.donation.databinding.ActivityCreateRequestBinding
import kotlinx.coroutines.CoroutineScope
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
            requiredUnitStr.toInt() <= 1 -> {
                showToast("Please enter a valid required unit greater than one")
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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.createRequest(bloodRequest)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val bloodResponse = response.body()
                        Toast.makeText(this@CreateRequestActivity, "Request ID: ${bloodResponse?.id}", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@CreateRequestActivity, SentSuccessfullActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("CreateRequestActivity", "Error: $errorBody")
                        Toast.makeText(this@CreateRequestActivity, "Failed to create request: $errorBody", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Log.e("CreateRequestActivity", "Network Error: ${e.message}")
                    Toast.makeText(this@CreateRequestActivity, "Network Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Log.e("CreateRequestActivity", "HTTP Error: ${e.message}")
                    Toast.makeText(this@CreateRequestActivity, "HTTP Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("CreateRequestActivity", "Unexpected Error: ${e.message}")
                    Toast.makeText(this@CreateRequestActivity, "Unexpected Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
