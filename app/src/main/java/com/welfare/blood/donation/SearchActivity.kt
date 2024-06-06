package com.welfare.blood.donation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.welfare.blood.donation.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var selectedBloodGroup: String

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
    }

    private val bloodGroupClickListener = View.OnClickListener { v ->
        val button = v as Button
        selectedBloodGroup = button.text.toString()
        binding.tvSelectedGroup.text = "$selectedBloodGroup"
        binding.btnSearch.isEnabled = true
    }

    private fun searchPeopleByBloodGroup() {
        // Dummy data for demonstration.
        val allPeople = listOf(
            Patient("John Doe", "john.doe@example.com", "123 Main St", "A+", "https://example.com/john_doe.jpg"),
            Patient("Jane Smith", "jane.smith@example.com", "456 Elm St", "AB+", "https://example.com/jane_smith.jpg"),
            Patient("Robert Brown", "robert.brown@example.com", "789 Oak St", "B+", "https://example.com/robert_brown.jpg"),
            Patient("Emily White", "emily.white@example.com", "321 Maple St", "O+", "https://example.com/emily_white.jpg")
        )
        val peopleList = allPeople.filter { it.bloodGroup == selectedBloodGroup }

        // Launch SearchResultActivity with the search results
        val intent = Intent(this, SearchResultActivity::class.java).apply {
            putParcelableArrayListExtra("searchResults", ArrayList(peopleList))
        }
        startActivity(intent)
    }
}
