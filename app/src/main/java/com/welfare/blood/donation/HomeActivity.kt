package com.welfare.blood.donation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.cardview.widget.CardView

import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.welfare.blood.donation.fragments.HistoryFragment
import com.welfare.blood.donation.fragments.HomeFragment
import com.welfare.blood.donation.fragments.InboxFragment
import com.welfare.blood.donation.fragments.NotificationFragment

  class HomeActivity : AppCompatActivity() {
   // private val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val cardView1 = findViewById<CardView>(R.id.cardview1)
        val cardView2 = findViewById<CardView>(R.id.cardview2)
        val cardView3 = findViewById<CardView>(R.id.cardview3)
        val cardView4 = findViewById<CardView>(R.id.cardview4)
        val homeFragment=HomeFragment()
        val inboxFragment=InboxFragment()
        val notificationFragment=NotificationFragment()
        val historyFragment=HistoryFragment()

        cardView1.setOnClickListener {
            val intent = Intent(this, CreateRequestActivity::class.java)
            startActivity(intent)
        }
        cardView2.setOnClickListener {
            val intent = Intent(this, BloodDonorActivity::class.java)
            startActivity(intent)
        }
        cardView3.setOnClickListener {
            val intent = Intent(this, DonateBloodActivity::class.java)
            startActivity(intent)
        }

//        cardView4.setOnClickListener {
//            val intent = Intent(this, DonateBloodActivity::class.java)
//            startActivity(intent)
//        }
        setCurrentFragment(homeFragment)

//        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.home -> setCurrentFragment(homeFragment)
//                R.id.inbox -> setCurrentFragment(inboxFragment)
//                R.id.notification -> setCurrentFragment(notificationFragment)
//                R.id.history -> setCurrentFragment(historyFragment)
//            }
//            true
//        }

    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()

    }
}

