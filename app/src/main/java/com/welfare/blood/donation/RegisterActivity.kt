package com.welfare.blood.donation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.welfare.blood.donation.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUp.setOnClickListener {
            val enteredUsername = binding.edName.text.toString()
            val enteredPassword = binding.edPassword.text.toString()
            val enteredEmail = binding.edEmail.text.toString()
            val enteredPhone = binding.edPhone.text.toString()
            val enteredDateBirth = binding.edDateBirth.text.toString()
            val donateBlood = binding.noYes.isChecked

            if (enteredUsername.isEmpty() || enteredPassword.isEmpty() || enteredEmail.isEmpty() || enteredPhone.isEmpty() || enteredDateBirth.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (enteredUsername.length < 6) {
                Toast.makeText(this, "Username must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            } else if (enteredPassword.length < 8) {
                Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
            } else if (!containsSpecialCharacter(enteredPassword)) {
                Toast.makeText(this, "Password must contain at least one special character", Toast.LENGTH_SHORT).show()
            } else if (!isValidEmail(enteredEmail)) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            } else if (!isValidPhoneNumber(enteredPhone)) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            } else if (!isValidDateOfBirth(enteredDateBirth)) {
                Toast.makeText(this, "Please enter a valid date of birth (yyyy-MM-dd)", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    val phoneNumber = enteredPhone.toLong()
                    registerUser(enteredUsername, phoneNumber, enteredEmail, enteredPassword, enteredDateBirth, "O+", donateBlood)
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun registerUser(name: String, phoneNumber: Long, email: String, password: String, dateOfBirth: String, bloodGroup: String, donateBlood: Boolean) {
        val registerRequest = ApiService.RegisterRequest(
            name,
            phoneNumber,
            email,
            password,
            dateOfBirth,
            bloodGroup,
            donateBlood
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.registerUser(registerRequest)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Registration Successful: ${response.name}", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Registration Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Pattern.compile("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}\$")
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        val pattern = Pattern.compile("^\\d{10}\$")
        val matcher = pattern.matcher(phone)
        return matcher.matches()
    }

    private fun isValidDateOfBirth(dateOfBirth: String): Boolean {
        val pattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}\$")
        val matcher = pattern.matcher(dateOfBirth)
        return matcher.matches()
    }

    private fun containsSpecialCharacter(text: String): Boolean {
        val pattern = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]")
        val matcher = pattern.matcher(text)
        return matcher.find()
    }
}
