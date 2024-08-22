package com.welfare.blood.donation

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.welfare.blood.donation.databinding.ActivityHomeBinding
import com.welfare.blood.donation.fragments.BothDonationFragment
import com.welfare.blood.donation.fragments.HistoryFragment
import com.welfare.blood.donation.fragments.HomeFragment
import com.welfare.blood.donation.fragments.NotificationFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var titleTextView: TextView
    private lateinit var binding: ActivityHomeBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val bloodGroup = intent.getStringExtra("bloodGroup")
        val location = intent.getStringExtra("location")

        if (name != null && bloodGroup != null && location != null) {
            val intent = Intent(this, CreateRequestActivity::class.java).apply {
                putExtra("bloodFor", "Myself")
                putExtra("name", name)
                putExtra("bloodGroup", bloodGroup)
                putExtra("location", location)
            }
            startActivity(intent)
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_edit_profile -> {
                    navigateToEditProfile()
                    true
                }

                R.id.nav_blood_bank -> {
                    navigateToBloodBankActivity(this)
                    true
                }

                R.id.nav_policy -> {
                    navigateToPrivacyPolicy(this)
                    true
                }

                R.id.nav_share -> {
                    shareGoogleStore(this)
                    true
                }

                R.id.nav_rate_us -> {
                    showRateUsDialog(this)
                    true
                }

                R.id.nav_feedback -> {
                    showFeedbackDialog(this)
                    true
                }

                R.id.nav_logout -> {
                    showLogoutDialog(this)
                    true
                }

                R.id.nav_delete_profile -> {
                    promptForPassword()
                    true
                }

                else -> false
            }.also {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        binding.menuLine.setOnClickListener {
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            } else {

            }
        }

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        titleTextView = findViewById(R.id.title)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment(), getString(R.string.title_home))
        }

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment(), getString(R.string.title_home))
                    true
                }

                R.id.activity -> {
                    loadFragment(BothDonationFragment(), getString(R.string.title_activity))
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
    private fun performProfileDeletionWithPassword(password: String) {
        val user = auth.currentUser
        user?.let {
            val credential = EmailAuthProvider.getCredential(it.email!!, password)

            it.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    performProfileDeletion()
                } else {
                    Toast.makeText(this, "Re-authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun performProfileDeletion() {
        val user = auth.currentUser
        user?.let {
            db.collection("users").document(it.uid).delete().addOnCompleteListener { firestoreTask ->
                if (firestoreTask.isSuccessful) {
                    it.delete().addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                            Toast.makeText(this, "Profile deleted successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to delete profile: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            Log.e("ProfileDeletion", "Auth deletion failed", authTask.exception)
                        }
                    }
                } else {
                    Toast.makeText(this, "Failed to delete user data: ${firestoreTask.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun promptForPassword() {
        performProfileDeletion()
    }

    private fun showLogoutDialog(homeActivity: HomeActivity) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        alertDialog.setCancelable(false)

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            alertDialog.dismiss()
            performLogout()
        }

        alertDialog.show()
    }

    private fun performLogout() {
        auth.signOut()

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", false)
            apply()
        }

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    private fun showFeedbackDialog(homeActivity: HomeActivity) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rate_us, null)
        val editTextFeedback = dialogView.findViewById<EditText>(R.id.editTextFeedback)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.send).setOnClickListener {
            val feedback = editTextFeedback.text.toString().trim()
            if (feedback.isNotEmpty()) {
                Toast.makeText(this, "Thank you for your feedback: $feedback", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Please Enter Your Feedback", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.findViewById<Button>(R.id.cancel).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showRateUsDialog(homeActivity: HomeActivity) {

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

    private fun navigateToPrivacyPolicy(homeActivity: HomeActivity) {
        val intent = Intent(this, PrivacyPolicyActivity::class.java)
        startActivity(intent)


    }

    private fun navigateToBloodBankActivity(homeActivity: HomeActivity) {
        val intent = Intent(this, BloodbankActivity::class.java)
        startActivity(intent)

    }

    private fun navigateToEditProfile() {
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
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

        updateTitle(title)
        updateMenuLineVisibility(fragment is HomeFragment)
    }

    private fun updateTitle(title: String) {
        titleTextView.text = title
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
    private fun updateMenuLineVisibility(show: Boolean) {
        if (show) {
            binding.menuLine.visibility = View.VISIBLE
        } else {
            binding.menuLine.visibility = View.GONE
        }
    }
}
