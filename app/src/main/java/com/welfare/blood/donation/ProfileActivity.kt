package com.welfare.blood.donation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.welfare.blood.donation.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
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

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        loadUserProfile()

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE)
        }

        binding.btnDeleteProfile.setOnClickListener {
            deleteProfileImage()
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

                val imageUrl = document.getString("profileImageUrl")
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_profil_menu)
                        .error(R.drawable.ic_profil_menu)
                        .into(binding.ivProfileImage)

                    binding.btnDeleteProfile.isEnabled = true // Enable delete button if image exists
                } else {
                    binding.ivProfileImage.setImageResource(R.drawable.ic_profil_menu)
                    binding.btnDeleteProfile.isEnabled = false // Disable delete button if no image
                }
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

    private fun deleteProfileImage() {
        val user = auth.currentUser ?: return
        val userId = user.uid

        val fileRef = storage.reference.child("profile_images/$userId")

        fileRef.delete().addOnSuccessListener {
            val userMap = hashMapOf(
                "profileImageUrl" to ""
            )

            db.collection("users").document(userId).set(userMap, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile Image Deleted", Toast.LENGTH_SHORT).show()
                    loadUserProfile() // Reload profile to update UI
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to delete profile image", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to delete profile image", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val EDIT_PROFILE_REQUEST_CODE = 1001
    }
}
