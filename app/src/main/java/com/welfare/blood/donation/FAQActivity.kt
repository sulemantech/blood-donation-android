package com.welfare.blood.donation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.welfare.blood.donation.databinding.ActivityFaqactivityBinding

class FAQActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaqactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFaqactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

    }
}