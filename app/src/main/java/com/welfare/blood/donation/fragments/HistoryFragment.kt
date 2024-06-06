package com.welfare.blood.donation.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
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
import com.welfare.blood.donation.R
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
                    performLogout()
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
                showToast("Please enter your feedback")
            }
        }

        dialogView.findViewById<Button>(R.id.cancel).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun showFeedbackAndSuggestion() {
        // Show feedback and suggestion implementation
    }

    private fun performLogout() {
        // Perform logout implementation
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Release binding
    }
}
