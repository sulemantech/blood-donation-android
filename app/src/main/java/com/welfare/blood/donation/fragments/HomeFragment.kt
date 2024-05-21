package com.welfare.blood.donation.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import com.welfare.blood.donation.BloodDonorActivity
import com.welfare.blood.donation.CreateRequestActivity
import com.welfare.blood.donation.DonateBloodActivity
import com.welfare.blood.donation.R
import com.welfare.blood.donation.databinding.FragmentHome2Binding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHome2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHome2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardView1 = binding.cardview1
        val cardView2 = binding.cardview2
        val cardView3 = binding.cardview3
        val cardView4 = binding.cardview4

        cardView1.setOnClickListener {
            val intent = Intent(requireContext(), CreateRequestActivity::class.java)
            startActivity(intent)
        }

        cardView2.setOnClickListener {
            val intent = Intent(requireContext(), DonateBloodActivity::class.java)
            startActivity(intent)
        }

        cardView3.setOnClickListener {
            val intent = Intent(requireContext(), BloodDonorActivity::class.java)
            startActivity(intent)
        }
//        cardView4.setOnClickListener {
//            val intent = Intent(requireContext(), HistoryFragment::class.java)
//            startActivity(intent)
//        }
    }
}
