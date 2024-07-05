package com.welfare.blood.donation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        loadUserProfile()

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.ivCamera.setOnClickListener {
            pickImageFromGallery()
        }

        binding.btnDeleteProfile.setOnClickListener {
            deleteProfileImage()
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

                val imageUrl = document.getString("profileImageUrl")
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.baseline_person_outline_24)
                        .error(R.drawable.baseline_person_outline_24)
                        .into(binding.ivProfileImage)

                    binding.btnDeleteProfile.isEnabled = true // Enable delete button if image exists
                } else {
                    binding.ivProfileImage.setImageResource(R.drawable.baseline_person_outline_24)
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

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            uploadImageToFirebase(imageUri)
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri?) {
        if (imageUri == null) return

        val user = auth.currentUser ?: return
        val userId = user.uid

        val fileRef = storage.reference.child("profile_images/$userId")

        val uploadTask = fileRef.putFile(imageUri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            fileRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val imageUrl = downloadUri.toString()
                updateUserProfileImage(imageUrl)
            } else {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserProfileImage(imageUrl: String) {
        val user = auth.currentUser ?: return
        val userId = user.uid

        val userMap = hashMapOf(
            "profileImageUrl" to imageUrl
        )

        db.collection("users").document(userId).set(userMap, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Profile Image Updated", Toast.LENGTH_SHORT).show()
                loadUserProfile() // Reload profile to display updated image
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update profile image", Toast.LENGTH_SHORT).show()
            }
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
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to delete profile image", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to delete profile image", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}
