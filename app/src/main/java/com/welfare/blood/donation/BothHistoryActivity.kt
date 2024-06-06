package com.welfare.blood.donation

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
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

        binding.btnMyHistory.setOnClickListener {
            showMyHistoryLayout()
        }

        binding.btnRequestHistory.setOnClickListener {
            showRequestHistoryLayout()
        }

        binding.btnDonationHistory.setOnClickListener {
            showDonationHistoryLayout()
        }

        initRecyclerView()

        showMyHistoryLayout()
    }

    private fun showMyHistoryLayout() {
        resetButtonBackgrounds()
        binding.btnMyHistory.setBackgroundColor(Color.WHITE)

        binding.myHistoryRecyclerview.visibility = View.VISIBLE
        binding.requestRecyclerview.visibility = View.GONE
        binding.donationRecyclerview.visibility = View.GONE
    }

    private fun showRequestHistoryLayout() {
        resetButtonBackgrounds()
        binding.btnRequestHistory.setBackgroundColor(Color.WHITE)

        binding.myHistoryRecyclerview.visibility = View.GONE
        binding.requestRecyclerview.visibility = View.VISIBLE
        binding.donationRecyclerview.visibility = View.GONE
    }

    private fun showDonationHistoryLayout() {
        resetButtonBackgrounds()
        binding.btnDonationHistory.setBackgroundColor(Color.WHITE)

        binding.myHistoryRecyclerview.visibility = View.GONE
        binding.requestRecyclerview.visibility = View.GONE
        binding.donationRecyclerview.visibility = View.VISIBLE
    }

    private fun resetButtonBackgrounds() {
        val defaultColor = resources.getColor(R.color.background, null)
        binding.btnMyHistory.setBackgroundColor(defaultColor)
        binding.btnRequestHistory.setBackgroundColor(defaultColor)
        binding.btnDonationHistory.setBackgroundColor(defaultColor)
    }

    private fun initRecyclerView() {
        val myHistoryData = listOf(
            HistoryItem("Ali", "city hospital", "O+", "Requested", "A+", "19/03/2024", "03:55 PM"),
            HistoryItem("Saad", "murree", "A-", "Requested", "B+", "20/03/2024", "04:30 PM"),
        )

        val requestHistoryData = listOf(
            HistoryItem("Saim", "rwp", "B+", "Requested", "O-", "21/03/2024", "10:00 AM"),
            HistoryItem("Sana", "lhr", "AB+", "Requested", "A-", "22/03/2024", "02:15 PM"),
            // Add more dummy data here
        )

        val donationHistoryData = listOf(
            HistoryItem("Anas", "khr", "O-", "Donated", "AB+", "23/03/2024", "09:45 AM"),
            HistoryItem("Fatima", "isb", "A+", "Donated", "B-", "24/03/2024", "11:30 AM"),

        )

        val myHistoryAdapter = HistoryAdapter(myHistoryData)
        binding.myHistoryRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.myHistoryRecyclerview.adapter = myHistoryAdapter

        val requestHistoryAdapter = HistoryAdapter(requestHistoryData)
        binding.requestRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.requestRecyclerview.adapter = requestHistoryAdapter

        val donationHistoryAdapter = HistoryAdapter(donationHistoryData)
        binding.donationRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.donationRecyclerview.adapter = donationHistoryAdapter
    }
}
