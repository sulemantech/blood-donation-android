package com.welfare.blood.donation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

        val allItems: ArrayList<BloodBankItem>? = intent.getParcelableArrayListExtra("allItems")

        // Handle the case where the list is null or empty
        if (allItems != null && allItems.isNotEmpty()) {
        //    val adapter = BloodBankAdapter(allItems)
            binding.recyclerViewAllItems.layoutManager = LinearLayoutManager(this)
          //  binding.recyclerViewAllItems.adapter = adapter
        }
    }
}
