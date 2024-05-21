package com.welfare.blood.donation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.welfare.blood.donation.databinding.ActivityCreateRequestBinding
import com.welfare.blood.donation.databinding.ActivityDonateBloodBinding

class DonateBloodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDonateBloodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDonateBloodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backArrow.setOnClickListener{
            val intent= Intent(this,HomeActivity::class.java)
            startActivity(intent)
        }

    }
}