package com.welfare.blood.donation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.welfare.blood.donation.databinding.ActivityDonateBloodBinding
import com.welfare.blood.donation.databinding.ActivityForgetPasswordBinding
import java.util.concurrent.TimeUnit

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }
        binding.sendOtp.setOnClickListener {
            val intent = Intent(this,VerifyOtpActivity::class.java)
            startActivity(intent)
        }
        auth = FirebaseAuth.getInstance()

        binding.sendOtp.setOnClickListener {
            val phoneNumber = binding.edEmail.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                sendVerificationCode(phoneNumber)
            } else {
                binding.edEmail.error = "Enter a valid phone number"
            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@ForgetPasswordActivity, e.message, Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verificationId, token)
                    this@ForgetPasswordActivity.verificationId = verificationId
                    val intent = Intent(this@ForgetPasswordActivity, VerifyOtpActivity::class.java)
                    intent.putExtra("verificationId", verificationId)
                    startActivity(intent)
                    finish()
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.currentUser?.updatePhoneNumber(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@ForgetPasswordActivity, "Phone number added successfully", Toast.LENGTH_SHORT).show()
                    // Navigate to another activity if needed after phone number verification
                } else {
                    Toast.makeText(this@ForgetPasswordActivity, "Failed to add phone number: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }

    }
}