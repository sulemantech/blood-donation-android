package com.welfare.blood.donation

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.welfare.blood.donation.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var selectedBloodGroup: String
    private lateinit var adapter: PeopleAdapter
    private val peopleList = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        binding.btnAPlus.setOnClickListener(bloodGroupClickListener)
        binding.btnABPlus.setOnClickListener(bloodGroupClickListener)
        binding.btnBPlus.setOnClickListener(bloodGroupClickListener)
        binding.btnOPlus.setOnClickListener(bloodGroupClickListener)
        binding.btnSearch.setOnClickListener {
            searchPeopleByBloodGroup()
        }

        adapter = PeopleAdapter(peopleList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private val bloodGroupClickListener = View.OnClickListener { v ->
        val button = v as Button
        selectedBloodGroup = button.text.toString()
        binding.tvSelectedGroup.text = " $selectedBloodGroup"
        binding.btnSearch.isEnabled = true
    }

    private fun searchPeopleByBloodGroup() {
        // Dummy data for demonstration. In real scenarios, fetch from a database or API.
        val allPeople = listOf("John Doe - A+","Ali - A+", "Jane Smith - AB+","Ahmed - AB+", "Robert Brown - B+","Saad - B+", "Saim - O+", "Emily White - O+")
        peopleList.clear()

        for (person in allPeople) {
            if (person.contains(selectedBloodGroup)) {
                peopleList.add(person)
            }
        }

        adapter.notifyDataSetChanged()
    }
}
