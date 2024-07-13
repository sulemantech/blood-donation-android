package com.welfare.blood.donation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import com.welfare.blood.donation.databinding.ActivityResetCredentialBinding
import com.welfare.blood.donation.databinding.ActivityVerifyOtpBinding

class ResetCredentialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetCredentialBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityResetCredentialBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupEditTextFocusListeners()
        setupPasswordVisibilityToggle()
    }

    private fun setupPasswordVisibilityToggle() {
        binding.showPassword.setOnCheckedChangeListener {
                buttonView, isChecked ->
            if (isChecked) {
                binding.edPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.edPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.edPassword.setSelection(binding.edPassword.text.length)
        }
    }

    private fun setupEditTextFocusListeners() {
        binding.edConfirmPass.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.edConfirmPass.hint = ""
            } else {
                binding.edConfirmPass.hint = "Username"
            }
        }

        binding.edPassword.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.edPassword.hint = ""
            } else {
                binding.edPassword.hint = "Password"
            }
        }
    }
}