package com.welfare.blood.donation

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.adapters.CommunityAdapter
import com.welfare.blood.donation.databinding.ActivityAddDonorosBinding
import com.welfare.blood.donation.models.CommunityDonors
import java.util.Date

class AddDonorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddDonorosBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var communityAdapter: CommunityAdapter
    private var donorList = mutableListOf<CommunityDonors>()
    private var allDonors = mutableListOf<CommunityDonors>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDonorosBinding.inflate(layoutInflater)
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
            navigateToHome()
        }

        val autoCompleteCity: AutoCompleteTextView = binding.autoCompleteCity
        val cities = resources.getStringArray(R.array.pakistan_cities)
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, cities)
        autoCompleteCity.setAdapter(cityAdapter)

        autoCompleteCity.setOnItemClickListener { parent, view, position, id ->
            val selectedCity = parent.getItemAtPosition(position) as String
            filterDonors(selectedCity, binding.spinnerBloodgroup.selectedItem as String)
        }

        autoCompleteCity.addTextChangedListener {
            val typedCity = it.toString()
            filterDonors(typedCity, binding.spinnerBloodgroup.selectedItem as String)
        }

        val bloodGroupSpinner: Spinner = binding.spinnerBloodgroup
        val bloodGroups = resources.getStringArray(R.array.blood_groups)
        val bloodGroupAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroups)
        bloodGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodGroupSpinner.adapter = bloodGroupAdapter

        bloodGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedBloodGroup = parent.getItemAtPosition(position) as String
                filterDonors(autoCompleteCity.text.toString(), selectedBloodGroup)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        db = FirebaseFirestore.getInstance()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        communityAdapter = CommunityAdapter(donorList, ::onEditClick, ::onDeleteClick)
        binding.recyclerView.adapter = communityAdapter

        fetchDonorsAddedByAdmin()

        binding.cardview5.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("isAddedByAdmin", true)
            startActivity(intent)
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

    private fun fetchDonorsAddedByAdmin() {
        db.collection("users")
            .whereEqualTo("addedByAdmin", true)
            .whereEqualTo("userType", "user")
            .get()
            .addOnSuccessListener { querySnapshot ->
                allDonors.clear()
                donorList.clear()
                for (document in querySnapshot.documents) {
                    val communityDonors = document.toObject(CommunityDonors::class.java)
                    if (communityDonors != null) {
                        val isDeleted = document.getBoolean("isDeleted") ?: false
                        if (!isDeleted) {
                            communityDonors.userId = document.id
                            allDonors.add(communityDonors)
                            donorList.add(communityDonors)
                        }
                    }
                }

                donorList.sortByDescending { donor ->
                    donor.registrationTimestamp?.toDate() ?: Date(0)
                }

                communityAdapter.notifyDataSetChanged()
                displayRequestCount(donorList.size)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch donors", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterDonors(city: String, bloodGroup: String) {
        donorList.clear()
        val filteredDonors = allDonors.filter {
            (city.isEmpty() || it.location.equals(city, ignoreCase = true)) &&
                    (bloodGroup == "Select Blood Group" || it.bloodGroup == bloodGroup)
        }
        donorList.addAll(filteredDonors)
        communityAdapter.notifyDataSetChanged()
        displayRequestCount(donorList.size)
    }

    private fun displayRequestCount(count: Int) {
        binding.users.text = "Total Requests: $count"
    }

    private fun onEditClick(communityDonors: CommunityDonors) {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.putExtra("USER_ID", communityDonors.userId)
        intent.putExtra("isAddedByAdmin", true)
        startActivity(intent)
    }

    private fun onDeleteClick(communityDonors: CommunityDonors) {
        showDeleteConfirmationDialog(communityDonors)
    }

    private fun showDeleteConfirmationDialog(communityDonors: CommunityDonors) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Donor")
        builder.setMessage("Are you sure you want to delete this donor?")

        builder.setPositiveButton("Delete") { dialog: DialogInterface, _: Int ->
            deleteDonor(communityDonors)
        }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun deleteDonor(communityDonors: CommunityDonors) {
        if (communityDonors.userId.isNotEmpty()) {
            db.collection("users").document(communityDonors.userId)
                .update("isDeleted", true)
                .addOnSuccessListener {
                    Toast.makeText(this, "Donor deleted successfully", Toast.LENGTH_SHORT).show()
                    donorList.removeAll { it.userId == communityDonors.userId }
                    allDonors.removeAll { it.userId == communityDonors.userId }
                    communityAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.w("AddDonorsActivity", "Error deleting donor", e)
                    Toast.makeText(this, "Error deleting donor", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.e("AddDonorsActivity", "Invalid documentId: ${communityDonors.userId}")
            Toast.makeText(this, "Invalid donor ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
