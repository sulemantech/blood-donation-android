package com.welfare.blood.donation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import java.util.regex.Pattern
import com.welfare.blood.donation.databinding.ActivityLoginBinding
import com.welfare.blood.donation.fragments.HomeFragment

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.login.setOnClickListener {
            val enteredUsername = binding.edUsername.text.toString()
            val enteredPassword = binding.edPassword.text.toString()

            if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            } else if (enteredUsername.length < 6) {
                Toast.makeText(this, "Username must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            } else if (enteredPassword.length < 8) {
                Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
            } else if (!containsSpecialCharacter(enteredPassword)) {
                Toast.makeText(this, "Password must contain at least one special character", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }

        binding.reset.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun containsSpecialCharacter(text: String): Boolean {
        val pattern = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]")
        val matcher = pattern.matcher(text)
        return matcher.find()
    }
}
