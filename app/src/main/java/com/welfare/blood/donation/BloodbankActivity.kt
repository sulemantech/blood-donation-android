package com.welfare.blood.donation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.welfare.blood.donation.databinding.ActivityBloodbankBinding
import com.welfare.blood.donation.fragments.HistoryFragment

class BloodbankActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBloodbankBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBloodbankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

    }
}