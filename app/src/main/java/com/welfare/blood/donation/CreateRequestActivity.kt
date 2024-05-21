package com.welfare.blood.donation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.welfare.blood.donation.databinding.ActivityCreateRequestBinding

class CreateRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateRequestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCreateRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener{
            val intent=Intent(this,HomeActivity::class.java)
            startActivity(intent)
        }
        binding.btnDonate.setOnClickListener{
            val intent = Intent(this, SentSuccessfullActivity::class.java)
        startActivity(intent)

        }
    }
}