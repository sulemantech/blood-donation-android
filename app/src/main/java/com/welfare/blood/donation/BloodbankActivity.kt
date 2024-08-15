package com.welfare.blood.donation

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.welfare.blood.donation.adapters.BloodBankAdapter
import com.welfare.blood.donation.databinding.ActivityBloodbankBinding
import com.welfare.blood.donation.fragments.HistoryFragment

class BloodbankActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBloodbankBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBloodbankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bloodBankItems = listOf("Blood Bank 1", "Blood Bank 2", "Blood Bank 3", "Blood Bank 4")
        val adapter = BloodBankAdapter(bloodBankItems)
        binding.bloodbankRecyclerview.adapter = adapter

        binding.seeAllBanks.setOnClickListener {
            val intent = Intent(this, BloodBankAllItemsActivity::class.java)
            intent.putStringArrayListExtra("allItems", ArrayList(bloodBankItems))
            startActivity(intent)
        }

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        }

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }
}