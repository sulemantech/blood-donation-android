package com.welfare.blood.donation

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.welfare.blood.donation.databinding.ActivityBothHistoryBinding

class BothHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBothHistoryBinding

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBothHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        // Set onClickListeners for the buttons
        binding.btnMyHistory.setOnClickListener {
            showMyHistoryLayout()
        }

        binding.btnRequestHistory.setOnClickListener {
            showRequestHistoryLayout()
        }

        binding.btnDonationHistory.setOnClickListener {
            showDonationHistoryLayout()
        }

        // Initially show the My History layout
        showMyHistoryLayout()
    }

    private fun showMyHistoryLayout() {
        binding.myHistoryLayout.visibility = View.VISIBLE
        binding.requestHistoryLayout.visibility = View.GONE
        binding.donationHistoryLayout.visibility = View.GONE
    }

    private fun showRequestHistoryLayout() {
        binding.myHistoryLayout.visibility = View.GONE
        binding.requestHistoryLayout.visibility = View.VISIBLE
        binding.donationHistoryLayout.visibility = View.GONE
    }

    private fun showDonationHistoryLayout() {
        binding.myHistoryLayout.visibility = View.GONE
        binding.requestHistoryLayout.visibility = View.GONE
        binding.donationHistoryLayout.visibility = View.VISIBLE
    }
}
