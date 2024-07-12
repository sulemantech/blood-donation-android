package com.welfare.blood.donation.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.BothHistoryActivity
import com.welfare.blood.donation.DonateBloodActivity
import com.welfare.blood.donation.R
import com.welfare.blood.donation.ReceivedRequestsActivity
import com.welfare.blood.donation.SearchActivity
import com.welfare.blood.donation.databinding.FragmentHome2Binding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHome2Binding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHome2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val cardView1 = binding.cardview1
        val cardView2 = binding.cardview2
        val cardView3 = binding.cardview3
        val cardView4 = binding.cardview4

        cardView1.setOnClickListener {
            val intent = Intent(requireContext(), ReceivedRequestsActivity::class.java)
            startActivity(intent)
        }

        cardView2.setOnClickListener {
            val intent = Intent(requireContext(), DonateBloodActivity::class.java)
            startActivity(intent)
        }

        cardView3.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }
        cardView4.setOnClickListener {
            val intent = Intent(requireContext(), BothHistoryActivity::class.java)
            startActivity(intent)
        }
    }
}
