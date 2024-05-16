package com.welfare.blood.donation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.welfare.blood.donation.databinding.ActivityBloodbankBinding

class BloodbankActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBloodbankBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBloodbankBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}