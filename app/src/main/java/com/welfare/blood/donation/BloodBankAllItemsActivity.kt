package com.welfare.blood.donation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.welfare.blood.donation.adapters.BloodBankAdapter
import com.welfare.blood.donation.databinding.ActivityBloodBankAllItemsBinding

class BloodBankAllItemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBloodBankAllItemsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBloodBankAllItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val allItems = intent.getStringArrayListExtra("allItems") ?: listOf()

        val adapter = BloodBankAdapter(allItems)
        binding.recyclerViewAllItems.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAllItems.adapter = adapter
    }
}
