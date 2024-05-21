package com.welfare.blood.donation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.welfare.blood.donation.databinding.ActivitySentSuccessfullBinding

class SentSuccessfullActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySentSuccessfullBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySentSuccessfullBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backArrow.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        binding.homeBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}