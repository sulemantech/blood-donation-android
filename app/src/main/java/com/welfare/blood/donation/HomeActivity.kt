package com.welfare.blood.donation

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.welfare.blood.donation.fragments.HistoryFragment
import com.welfare.blood.donation.fragments.HomeFragment
import com.welfare.blood.donation.fragments.InboxFragment
import com.welfare.blood.donation.fragments.NotificationFragment
import com.welfare.blood.donation.fragments.ReceivedRequestsFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Load the HomeFragment initially
        if (savedInstanceState == null) {
            loadFragment(HomeFragment(), getString(R.string.title_home))
        }

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment(), getString(R.string.title_home_screen))
                    true
                }
                R.id.activity -> {
                    loadFragment(ReceivedRequestsFragment(), getString(R.string.title_activity))
                    true
                }
                R.id.notification -> {
                    loadFragment(NotificationFragment(), getString(R.string.title_notification))
                    true
                }
                R.id.history -> {
                    loadFragment(HistoryFragment(), getString(R.string.title_history))
                    true
                }
                else -> false
            }
        }
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    private fun loadFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
        setTitle(title)
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
        if (currentFragment is HomeFragment) {
            finish()
        } else {
            loadFragment(HomeFragment(), getString(R.string.title_home))
            bottomNavigationView.selectedItemId = R.id.home
        }
    }

    private fun setTitle(title: String) {
        supportActionBar?.title = title
    }
}
