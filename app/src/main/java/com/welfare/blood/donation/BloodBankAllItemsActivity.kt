package com.welfare.blood.donation

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.welfare.blood.donation.adapters.BloodBankAdapter
import com.welfare.blood.donation.databinding.ActivityBloodBankAllItemsBinding
import com.welfare.blood.donation.models.BloodBankItem

class BloodBankAllItemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBloodBankAllItemsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBloodBankAllItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            onBackPressed()
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

        val allItems: ArrayList<BloodBankItem>? = intent.getParcelableArrayListExtra("allItems")

        // Handle the case where the list is null or empty
        if (allItems != null && allItems.isNotEmpty()) {
        //    val adapter = BloodBankAdapter(allItems)
            binding.recyclerViewAllItems.layoutManager = LinearLayoutManager(this)
          //  binding.recyclerViewAllItems.adapter = adapter
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
