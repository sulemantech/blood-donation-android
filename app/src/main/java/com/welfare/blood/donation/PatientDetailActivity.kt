package com.welfare.blood.donation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.welfare.blood.donation.databinding.ActivityPatientDetailBinding

class PatientDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val patient = intent.getParcelableExtra<Patient>("patientDetail")

//        patient?.let {
//            binding.tvPatientDetail.text = """
//                Name: ${it.name}
//                Email: ${it.email}
//                Address: ${it.address}
//                Blood Group: ${it.bloodGroup}
//            """.trimIndent()
//
//            Glide.with(this)
//                .load(it.imageUrl)
//                .into(binding.ivProfileImage)
      //  }
    }
}

