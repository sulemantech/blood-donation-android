package com.welfare.blood.donation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserProfile()

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser ?: return
        val userId = user.uid

        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document != null) {
                binding.tvName.text = document.getString("name")
                binding.tvEmail.text = document.getString("email")
                binding.tvPhone.text = document.getString("phone")
                binding.tvLocation.text = document.getString("location")
                binding.tvGender.text = document.getString("gender") ?: "Add your Gender"
                binding.tvBirthday.text = document.getString("dateOfBirth") ?: "Add your Birthday"
                binding.pbProfileCompletion.progress = calculateProfileCompletion(document)
                binding.tvProfileCompletion.text = "Profile completion ${binding.pbProfileCompletion.progress}%"
            }
        }
    }

    private fun calculateProfileCompletion(document: DocumentSnapshot): Int {
        var completedFields = 0
        val totalFields = 7 // Number of fields used to calculate profile completion

        if (document["name"] != null) completedFields++
        if (document["email"] != null) completedFields++
        if (document["phone"] != null) completedFields++
        if (document["location"] != null) completedFields++
        if (document["gender"] != null) completedFields++
        if (document["dateOfBirth"] != null) completedFields++
        if (document["bloodType"] != null) completedFields++

        return (completedFields * 100) / totalFields
    }
}
