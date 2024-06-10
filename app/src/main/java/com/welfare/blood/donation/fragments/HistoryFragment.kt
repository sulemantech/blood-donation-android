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
import com.welfare.blood.donation.BloodbankActivity
import com.welfare.blood.donation.BothHistoryActivity
import com.welfare.blood.donation.CreateRequestActivity
import com.welfare.blood.donation.DonateBloodActivity
import com.welfare.blood.donation.FAQActivity
import com.welfare.blood.donation.LoginActivity
import com.welfare.blood.donation.R
import com.welfare.blood.donation.SearchActivity
import com.welfare.blood.donation.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

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

        val cardView1 = binding.cardview1
        val cardView2 = binding.cardview2
        val cardView3 = binding.cardview3
        val cardView4 = binding.cardview4

        cardView1.setOnClickListener {
//            val intent = Intent(requireContext(), CreateRequestActivity::class.java)
//            startActivity(intent)
        }

        cardView2.setOnClickListener {
            val intent = Intent(requireContext(), BloodbankActivity::class.java)
            startActivity(intent)
        }

        cardView3.setOnClickListener {
            val intent = Intent(requireContext(), FAQActivity::class.java)
            startActivity(intent)
        }
        cardView4.setOnClickListener {
           sharegooglestore(requireContext())
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
                R.id.nav_share -> {
                    sharegooglestore(requireContext())
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
                else -> false
            }.also {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        // Set up the drawer toggle
        binding.menuLine.setOnClickListener {
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun navigateToEditProfile() {
        // Open edit profile activity
        // startActivity(Intent(requireContext(), EditProfileActivity::class.java))
    }

    private fun navigateToBloodBankActivity(context: Context) {
        val intent = Intent(context, BloodbankActivity::class.java)
        context.startActivity(intent)
    }

    private fun sharegooglestore(context: Context) {
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

    private fun showFeedbackAndSuggestion() {
        // Show feedback and suggestion implementation
    }

    private fun showLogoutDialog(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_logout, null)

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        // Make the dialog non-cancelable
        alertDialog.setCancelable(false)

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            // Handle logout logic here
            alertDialog.dismiss()
            performLogout()
        }

        alertDialog.show()
    }

    private fun performLogout() {
        // Your logout logic here, e.g., clearing user data, navigating to login screen, etc.
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
