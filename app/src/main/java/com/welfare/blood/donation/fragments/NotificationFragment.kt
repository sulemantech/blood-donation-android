package com.welfare.blood.donation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.welfare.blood.donation.AppDatabase
import com.welfare.blood.donation.R
import com.welfare.blood.donation.adapters.NotificationAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationFragment : Fragment() {

    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        notificationRecyclerView = view.findViewById(R.id.notificationRecyclerView)
        notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadNotifications()

        return view
    }

    private fun loadNotifications() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(requireContext())
            val notifications = db.notificationDao().getAllNotifications()

            withContext(Dispatchers.Main) {
                notificationAdapter = NotificationAdapter(notifications)
                notificationRecyclerView.adapter = notificationAdapter
            }
        }
    }
}
