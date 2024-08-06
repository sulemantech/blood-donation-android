package com.welfare.blood.donation.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.CreateRequestActivity
import com.welfare.blood.donation.RequestAdapter
import com.welfare.blood.donation.databinding.FragmentRequestHistoryBinding
import com.welfare.blood.donation.models.Request
import java.text.SimpleDateFormat
import java.util.Locale
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.welfare.blood.donation.AllDonorsActivity

class RequestHistoryFragment : Fragment() {

    private lateinit var binding: FragmentRequestHistoryBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var requestList: MutableList<Request>
    private lateinit var adapter: RequestAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRequestHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        requestList = mutableListOf()
        adapter = RequestAdapter(requestList, ::onEditClick, ::onDeleteClick, ::showAllDonors)
        binding.recyclerView.adapter = adapter

        fetchRequests()
    }

    private fun fetchRequests() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.w("RequestHistoryFragment", "User not logged in")
            return
        }

        db.collection("requests")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                requestList.clear()
                for (document in documents) {
                    val request = document.toObject(Request::class.java).apply { id = document.id }

                    if (document.contains("isDeleted") && document.getBoolean("isDeleted") == true) {
                        continue
                    }
                    requestList.add(request)
                }

                requestList.sortByDescending {
                    dateFormat.parse(it.dateRequired)
                }

                adapter.notifyDataSetChanged()
                displayRequestCount(requestList.size)
            }
            .addOnFailureListener { e ->
                Log.w("RequestHistoryFragment", "Error fetching requests", e)
            }
    }

    private fun displayRequestCount(count: Int) {
        binding.receivedRequestCount.text = "Total Requests: $count"
    }

    private fun onEditClick(request: Request) {
        val intent = Intent(requireContext(), CreateRequestActivity::class.java)
        intent.putExtra("REQUEST_ID", request.id)
        startActivity(intent)
    }

    private fun onDeleteClick(request: Request) {
        showDeleteConfirmationDialog(request)
    }

    private fun showDeleteConfirmationDialog(request: Request) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Request")
        builder.setMessage("Are you sure you want to delete this request?")

        builder.setPositiveButton("Delete") { dialog: DialogInterface, _: Int ->
            deleteRequest(request) // Proceed with delete
        }

        builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            dialog.dismiss() // Close the dialog
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showAllDonors(requestId: String) {
        val intent = Intent(requireContext(), AllDonorsActivity::class.java)
        intent.putExtra("REQUEST_ID", requestId)
        startActivity(intent)
    }

    private fun deleteRequest(request: Request) {
        db.collection("requests").document(request.id)
            .update("isDeleted", true) // Mark as deleted
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Request deleted successfully", Toast.LENGTH_SHORT).show()
                // Remove the request from the local list immediately
                requestList.removeAll { it.id == request.id }
                adapter.notifyDataSetChanged() // Notify adapter of changes
                displayRequestCount(requestList.size) // Update request count
            }
            .addOnFailureListener { e ->
                Log.w("RequestHistoryFragment", "Error deleting request", e)
                Toast.makeText(requireContext(), "Error deleting request", Toast.LENGTH_SHORT).show()
            }
    }
}
