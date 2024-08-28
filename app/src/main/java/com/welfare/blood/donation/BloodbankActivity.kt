package com.welfare.blood.donation

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.welfare.blood.donation.adapters.BloodBankAdapter
import com.welfare.blood.donation.databinding.ActivityBloodbankBinding
import com.welfare.blood.donation.models.BloodBankItem

class BloodbankActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBloodbankBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBloodbankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bloodBankItems = listOf(
            BloodBankItem("Blood Bank 1", "123-456-7890", "Address 1"),
            BloodBankItem("Blood Bank 2", "234-567-8901", "Address 2"),
            BloodBankItem("Blood Bank 3", "345-678-9012", "Address 3"),
            BloodBankItem("Blood Bank 4", "456-789-0123", "Address 4"),
            BloodBankItem("Blood Bank 5", "567-890-1234", "Address 5"),
            BloodBankItem("Blood Bank 6", "678-901-2345", "Address 6"),
            BloodBankItem("Blood Bank 7", "789-012-3456", "Address 7"),
          //  BloodBankItem("Blood Bank 8", "890-123-4567", "Address 8")
        )


        setupRecyclerView(bloodBankItems)
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

        // Handle back button click
        binding.backArrow.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView(items: List<BloodBankItem>) {
        val adapter = BloodBankAdapter(items)
        binding.bloodbankRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.bloodbankRecyclerview.adapter = adapter
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
