package com.welfare.blood.donation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Check if the activity was started by a notification
        handleNotificationIntent()

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

        // Request notification permission after splash screen if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        } else {
            navigateToNextScreen()
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

    private fun requestNotificationPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission granted, proceed with next screen
                    navigateToNextScreen()
                } else {
                    // Permission denied, handle the situation or proceed
                    navigateToNextScreen()
                }
            }

        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                navigateToNextScreen()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS) -> {
                // Show rationale and request permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            else -> {
                // Directly request for permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun navigateToNextScreen() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

            if (isLoggedIn) {
                val intent = Intent(this, MainActivity::class.java) // Change to your main activity if needed
                val extras = intent.extras
                if (extras?.getString("openFragment") == "NotificationFragment") {
                    intent.putExtra("openFragment", "NotificationFragment")
                }
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)
    }

    private fun handleNotificationIntent() {
        val openFragment = intent.getStringExtra("openFragment")
        if (openFragment == "NotificationFragment") {
            intent.removeExtra("openFragment")
            navigateToNotificationFragment()
        }
    }

    private fun navigateToNotificationFragment() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("openFragment", "NotificationFragment")
        startActivity(intent)
        finish()
    }
}
