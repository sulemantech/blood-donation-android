package com.welfare.blood.donation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.welfare.blood.donation.databinding.ActivityVerifyOtpBinding

class VerifyOtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyOtpBinding
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }
        auth = FirebaseAuth.getInstance()

        verificationId = intent.getStringExtra("verification_id")

        binding.sendOtp.setOnClickListener {
            val code = binding.edEmail.text.toString()
            if (code.isNotEmpty() && verificationId != null) {
                val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid
                    Toast.makeText(this, "Phone verified successfully!", Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                } else {
                    Log.w(
                        "OtpVerificationActivity",
                        "Sign in with credential failed",
                        task.exception
                    )
                    Toast.makeText(
                        this,
                        "Verification failed. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}