package com.welfare.blood.donation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.welfare.blood.donation.databinding.ActivitySearchResultBinding

class SearchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultBinding
    private lateinit var adapter: PeopleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchResults = intent.getParcelableArrayListExtra<Patient>("searchResults") ?: arrayListOf()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PeopleAdapter(searchResults)
        binding.recyclerView.adapter = adapter

        adapter.setOnItemClickListener { person ->
            val intent = Intent(this, PatientDetailActivity::class.java).apply {
                putExtra("patientDetail", person)
            }
            startActivity(intent)
        }
    }
}
