package com.welfare.blood.donation

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.welfare.blood.donation.databinding.ActivityForgetPasswordBinding
import java.util.concurrent.TimeUnit

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var mAuth: FirebaseAuth
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.apply {
            idBtnGetOtp.setOnClickListener {
                val phoneNumber = "+92${idEdtPhoneNumber.text.toString().trim()}"
                if (TextUtils.isEmpty(idEdtPhoneNumber.text.toString().trim())) {
                    Toast.makeText(this@ForgetPasswordActivity, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show()
                } else {
                    sendVerificationCode(phoneNumber)
                }
            }

            idBtnVerify.setOnClickListener {
                if (TextUtils.isEmpty(idEdtOtp.text.toString().trim())) {
                    Toast.makeText(this@ForgetPasswordActivity, "Please enter OTP", Toast.LENGTH_SHORT).show()
                } else {
                    verifyCode(idEdtOtp.text.toString().trim())
                }
            }
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this@ForgetPasswordActivity, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@ForgetPasswordActivity, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(s, forceResendingToken)
            verificationId = s
        }

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            val code = phoneAuthCredential.smsCode
            if (!code.isNullOrBlank()) {
                binding.idEdtOtp.setText(code)
                verifyCode(code)
            } else {
                signInWithCredential(phoneAuthCredential)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@ForgetPasswordActivity, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun verifyCode(code: String) {
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
            signInWithCredential(credential)
        } else {
            Toast.makeText(this, "Verification ID is null", Toast.LENGTH_SHORT).show()
        }
    }
}
