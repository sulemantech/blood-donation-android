package com.welfare.blood.donation.fragments
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.welfare.blood.donation.BloodbankActivity
import com.welfare.blood.donation.R

class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.blood_bank).setOnClickListener {
            navigateToBloodBankActivity(requireContext())
        }

        view.findViewById<View>(R.id.share).setOnClickListener {
            shareApp(requireContext())
        }
        view.findViewById<View>(R.id.rate_us).setOnClickListener {
            showRateUsDialog(requireContext())
        }
    }


    private fun navigateToBloodBankActivity(context: Context) {
        val intent = Intent(context, BloodbankActivity::class.java)
        context.startActivity(intent)
    }

    private fun shareApp(context: Context) {
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
        AlertDialog.Builder(context)
            .setTitle("Rate Our App")
            .setMessage("If you enjoy using our app, please take a moment to rate it. Thank you for your support!")
            .setPositiveButton("Rate Now") { dialog, which ->
                // Open Play Store for rating
                openPlayStoreForRating(context)
            }
            .setNegativeButton("Later") { dialog, which ->
                // Do nothing, just close the dialog
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }
 // added test line
    private fun openPlayStoreForRating(context: Context) {
        val appPackageName = context.packageName
        try {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: android.content.ActivityNotFoundException) {
            // If Play Store app is not available, open the app page in browser
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}

