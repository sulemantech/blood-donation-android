package com.welfare.blood.donation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var db: FirebaseFirestore
    private var selectedBloodGroup: String? = null
    private var selectedCity: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        db = FirebaseFirestore.getInstance()

        binding.btnAPlus.setOnClickListener { setSelectedBloodGroup("A+") }
        binding.btnAMinus.setOnClickListener { setSelectedBloodGroup("A-") }
        binding.btnABPlus.setOnClickListener { setSelectedBloodGroup("AB+") }
        binding.btnABMinus.setOnClickListener { setSelectedBloodGroup("AB-") }
        binding.btnBPlus.setOnClickListener { setSelectedBloodGroup("B+") }
        binding.btnBMinus.setOnClickListener { setSelectedBloodGroup("B-") }
        binding.btnOPlus.setOnClickListener { setSelectedBloodGroup("O+") }
        binding.btnOMinus.setOnClickListener { setSelectedBloodGroup("O-") }

        val cities = resources.getStringArray(R.array.pakistan_cities)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.adapter = adapter

        binding.spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCity = cities[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        binding.btnSearch.setOnClickListener {
            if (selectedBloodGroup.isNullOrEmpty() || selectedCity.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a blood group and a city", Toast.LENGTH_SHORT).show()
            } else {
                saveSearchData(selectedBloodGroup!!, selectedCity!!)
            }
        }
    }

    private fun setSelectedBloodGroup(bloodGroup: String) {
        selectedBloodGroup = bloodGroup
        binding.tvSelectedGroup.text = bloodGroup
    }

    private fun saveSearchData(bloodGroup: String, city: String) {
        val searchData = hashMapOf(
            "bloodGroup" to bloodGroup,
            "city" to city
        )

        db.collection("searches")
            .add(searchData)
            .addOnSuccessListener { documentReference ->
                val intent = Intent(this, SearchResultActivity::class.java)
                intent.putExtra("searchId", documentReference.id)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving search data", Toast.LENGTH_SHORT).show()
            }
    }
}
