package com.welfare.blood.donation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.adapters.ViewPagerAdapter
import com.welfare.blood.donation.databinding.ActivityBothHistoryBinding

class BothHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBothHistoryBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBothHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.cardview5.setOnClickListener{
            val intent = Intent(this, CreateRequestActivity::class.java)
            startActivity(intent)
        }

        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "My Requests"
                1 -> "Others Request"
                else -> null
            }
        }.attach()
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
