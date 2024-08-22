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
import com.welfare.blood.donation.models.BloodBankItem

class BloodbankActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBloodbankBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBloodbankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sample data
        val bloodBankItems = listOf(
            BloodBankItem("Blood Bank 1", "123-456-7890", "Address 1"),
            BloodBankItem("Blood Bank 2", "234-567-8901", "Address 2"),
            BloodBankItem("Blood Bank 3", "345-678-9012", "Address 3"),
            BloodBankItem("Blood Bank 4", "456-789-0123", "Address 4")
        )

        // Display only the first 2 items initially
//        val initialItems = bloodBankItems.take(2)
//        val adapter = BloodBankAdapter(initialItems)
//        binding.bloodbankRecyclerview.adapter = adapter

        // Pass all items to the next activity
        binding.seeAllBanks.setOnClickListener {
            val intent = Intent(this, BloodBankAllItemsActivity::class.java)
            intent.putParcelableArrayListExtra("allItems", ArrayList(bloodBankItems))
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
