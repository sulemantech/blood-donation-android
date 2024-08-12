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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
