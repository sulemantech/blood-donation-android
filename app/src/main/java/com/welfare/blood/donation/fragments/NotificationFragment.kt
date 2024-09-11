package com.welfare.blood.donation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.welfare.blood.donation.AppDatabase
import com.welfare.blood.donation.adapters.NotificationAdapter
import com.welfare.blood.donation.databinding.FragmentNotificationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(context)
        fetchNotifications()
    }

    private fun fetchNotifications() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(requireContext())
                val notifications = db.notificationDao().getAllNotifications()

                launch(Dispatchers.Main) {
                    if (notifications.isNotEmpty()) {
                        binding.notificationRecyclerView.adapter = NotificationAdapter(notifications)
                    } else {
                        Toast.makeText(requireContext(), "No notifications found", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("NotificationFragment", "Error fetching notifications", e)
                Toast.makeText(requireContext(), "Error fetching notifications", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
