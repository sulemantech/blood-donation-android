package com.welfare.blood.donation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.welfare.blood.donation.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var selectedBloodGroup: String? = null
    private var selectedLocation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle back arrow click
        binding.backArrow.setOnClickListener {
            navigateToHome()
        }

        // Handle immersive layout flags
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

        // Setup the city spinner
        val locations = resources.getStringArray(R.array.pakistan_cities)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.adapter = adapter

        binding.spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLocation = locations[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }


        binding.btnAPlus.setOnClickListener {
            setSelectedBloodGroup("A+")
        }
        binding.btnAMinus.setOnClickListener {
            setSelectedBloodGroup("A-")
        }
        binding.btnBPlus.setOnClickListener {
            setSelectedBloodGroup("B+")
        }
        binding.btnBMinus.setOnClickListener {
            setSelectedBloodGroup("B-")
        }
        binding.btnABPlus.setOnClickListener {
            setSelectedBloodGroup("AB+")
        }
        binding.btnABMinus.setOnClickListener {
            setSelectedBloodGroup("AB-")
        }
        binding.btnOPlus.setOnClickListener {
            setSelectedBloodGroup("O+")
        }
        binding.btnOMinus.setOnClickListener {
            setSelectedBloodGroup("O-")
        }


        binding.btnSearch.setOnClickListener {
            if (selectedBloodGroup.isNullOrEmpty() || selectedLocation.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a blood group and a location", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, SearchResultActivity::class.java).apply {
                    putExtra("bloodGroup", selectedBloodGroup)
                    putExtra("location", selectedLocation)
                }
                startActivity(intent)
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
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

    private fun setSelectedBloodGroup(bloodGroup: String) {
        selectedBloodGroup = bloodGroup
        binding.tvSelectedGroup.text = bloodGroup
    }
}
