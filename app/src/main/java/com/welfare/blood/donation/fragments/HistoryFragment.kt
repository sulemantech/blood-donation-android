package com.welfare.blood.donation.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.*
import com.welfare.blood.donation.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.title_history)

        val cardView1 = binding.frameLayoutOne
        val cardView2 = binding.frameLayoutTwo
        val cardView3 = binding.frameLayoutThree
        val cardView4 = binding.frameLayoutFour

        cardView1.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        cardView4.setOnClickListener {
            val intent = Intent(requireContext(), BloodbankActivity::class.java)
            startActivity(intent)
        }

        cardView3.setOnClickListener {
            val intent = Intent(requireContext(), FAQActivity::class.java)
            startActivity(intent)
        }
        cardView2.setOnClickListener {
            shareGoogleStore(requireContext())
        }

        // Set up the navigation view
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_edit_profile -> {
                    navigateToEditProfile()
                    true
                }

                R.id.nav_blood_bank -> {
                    navigateToBloodBankActivity(requireContext())
                    true
                }

                R.id.nav_policy -> {
                    navigateToPrivacyPolicy(requireContext())
                    true
                }

                R.id.nav_share -> {
                    shareGoogleStore(requireContext())
                    true
                }

                R.id.nav_rate_us -> {
                    showRateUsDialog(requireContext())
                    true
                }

                R.id.nav_feedback -> {
                    showFeedbackDialog(requireContext())
                    true
                }

                R.id.nav_logout -> {
                    showLogoutDialog(requireContext())
                    true
                }

                R.id.nav_delete_profile -> {
                    promptForPassword()
                    true
                }

                else -> false
            }.also {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        // Set up the drawer toggle
        binding.menuLine.setOnClickListener {
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
    }

    private fun navigateToPrivacyPolicy(requireContext: Context) {

    }

    private fun navigateToEditProfile() {
        startActivity(Intent(requireContext(), EditProfileActivity::class.java))
    }

    private fun navigateToBloodBankActivity(context: Context) {
        val intent = Intent(context, BloodbankActivity::class.java)
        context.startActivity(intent)
    }

    private fun shareGoogleStore(context: Context) {
        val appPackageName = context.packageName
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app!")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Download the Blood Donation app from Google Play: https://play.google.com/store/apps/details?id=$appPackageName"
        )
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun showRateUsDialog(context: Context) {
        // Show rate us dialog implementation
    }

    private fun showFeedbackDialog(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rate_us, null)
        val editTextFeedback = dialogView.findViewById<EditText>(R.id.editTextFeedback)

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.send).setOnClickListener {
            val feedback = editTextFeedback.text.toString().trim()
            if (feedback.isNotEmpty()) {
                showToast("Thank you for your feedback: $feedback")
                alertDialog.dismiss()
            } else {
                showToast("Please Enter Your Feedback")
            }
        }

        dialogView.findViewById<Button>(R.id.cancel).setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.setCancelable(false)

        alertDialog.show()
    }

    private fun showLogoutDialog(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_logout, null)

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        alertDialog.setCancelable(false)

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            alertDialog.dismiss()
            performLogout()
        }

        alertDialog.show()
    }

    private fun promptForPassword() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_password_input, null)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.editTextPassword)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Re-authenticate")
            .setPositiveButton("Confirm") { _, _ ->
                val password = passwordEditText.text.toString().trim()
                if (password.isNotEmpty()) {
                    performProfileDeletionWithPassword(password)
                } else {
                    showToast("Please enter your password.")
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.show()
    }

    private fun performProfileDeletionWithPassword(password: String) {
        val user = auth.currentUser
        user?.let {
            val credential = EmailAuthProvider.getCredential(it.email!!, password)

            // Re-authenticate the user
            it.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    performProfileDeletion()
                } else {
                    showToast("Re-authentication failed. Please try again.")
                }
            }
        }
    }

    private fun performProfileDeletion() {
        val user = auth.currentUser
        user?.let {
            it.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Optionally, delete user data from Firestore if necessary
                    db.collection("users").document(it.uid).delete().addOnCompleteListener { firestoreTask ->
                        if (firestoreTask.isSuccessful) {
                            // Navigate to login screen
                            val intent = Intent(requireContext(), LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            requireActivity().finish() // Optional: Finish the current activity
                            showToast("Profile deleted successfully")
                        } else {
                            showToast("Failed to delete user data: ${firestoreTask.exception?.message}")
                        }
                    }
                } else {
                    showToast("Failed to delete profile: ${task.exception?.message}")
                }
            }
        }
    }

    private fun performLogout() {
        auth.signOut()

        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", false)
            apply()
        }

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        showToast("Logged out successfully")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
