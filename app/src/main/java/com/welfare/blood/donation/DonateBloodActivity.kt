package com.welfare.blood.donation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.welfare.blood.donation.databinding.ActivityDonateBloodBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DonateBloodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDonateBloodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonateBloodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        binding.donateButton.setOnClickListener {
            val bloodType = binding.bloodTypeEditText.text.toString()
            val location = binding.locationEditText.text.toString()

            if (bloodType.isNotEmpty() && location.isNotEmpty()) {
                donateBlood(bloodType, location)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun donateBlood(bloodType: String, location: String) {
        val request = ApiService.DonateBloodRequest(bloodType, location)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.donateBlood(request)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DonateBloodActivity, "Donation successful! ID: ${response.id}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DonateBloodActivity, "Donation failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
