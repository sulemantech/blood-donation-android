package com.welfare.blood.donation

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.welfare.blood.donation.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.register.setOnClickListener {
          startActivity(Intent(this, RegisterActivity::class.java))

        }
        binding.reset.setOnClickListener {
          startActivity(Intent(this, ForgetPasswordActivity::class.java))

        }
    }

    private fun loginUser() {
        val emailStr = binding.edUsername.text.toString().trim()
        val passwordStr = binding.edPassword.text.toString().trim()

        if (emailStr.isEmpty()) {
            binding.edUsername.error = "Email is required"
            binding.edUsername.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            binding.edUsername.error = "Please provide valid email"
            binding.edUsername.requestFocus()
            return
        }

        if (passwordStr.isEmpty()) {
            binding.edPassword.error = "Password is required"
            binding.edPassword.requestFocus()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(emailStr, passwordStr)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
    }
//    override fun onStart() {
//        super.onStart()
//
//        if (auth.currentUser != null) {
//            val intent = Intent(this, HomeActivity::class.java)
//            startActivity(intent)
//        }
//    }
}


//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.text.method.HideReturnsTransformationMethod
//import android.text.method.PasswordTransformationMethod
//import android.util.Log
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.welfare.blood.donation.databinding.ActivityLoginBinding
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class LoginActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityLoginBinding
//    private val apiService = RetrofitInstance.api
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Navigate to RegisterActivity when the register TextView is clicked
//        binding.register.setOnClickListener {
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
//        }
//
//        // Navigate to ForgetPasswordActivity when the forget password TextView is clicked
//        binding.forgetPassword.setOnClickListener {
//            val intent = Intent(this, ForgetPasswordActivity::class.java)
//            startActivity(intent)
//        }
//
//        // Toggle password visibility when the checkbox is checked/unchecked
//        binding.showPassword.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                // Show password
//                binding.edPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
//            } else {
//                // Hide password
//                binding.edPassword.transformationMethod = PasswordTransformationMethod.getInstance()
//            }
//        }
//
//        // Handle login button click
//        binding.btnLogin.setOnClickListener {
//            val username = binding.name.text.toString()
//            val password = binding.password.text.toString()
//
//            if (username.isNotEmpty() && password.isNotEmpty()) {
//                // Call login method if username and password are not empty
//                loginUser(username, password)
//            } else {
//                // Show a message if username or password is empty
//                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    // Method to login user
//    private fun loginUser(username: String, password: String) {
//        val loginRequest = ApiService.LoginRequest(name = username, password = password)
//
//        apiService.login(loginRequest).enqueue(object : Callback<ApiService.LoginResponse> {
//            override fun onResponse(call: Call<ApiService.LoginResponse>, response: Response<ApiService.LoginResponse>) {
//                if (response.isSuccessful) {
//                    val loginResponse = response.body()
//                    if (loginResponse != null && loginResponse.auth) {
//                        // Show success message and save the token
//                        Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
//                        saveToken(loginResponse.token)
//                        navigateToMainActivity()
//                    } else {
//                        // Show error message for incorrect username or password
//                        Toast.makeText(this@LoginActivity, "Incorrect username or password", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    // Show server error message
//                    val errorMessage = response.errorBody()?.string() ?: "Login Failed. Please try again later."
//                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<ApiService.LoginResponse>, t: Throwable) {
//                // Show network error message
//                Toast.makeText(this@LoginActivity, "Network Error. Please check your internet connection.", Toast.LENGTH_SHORT).show()
//                Log.e("LoginActivity", "Login failed", t)
//            }
//        })
//    }
//
//    // Method to save token in shared preferences
//    private fun saveToken(token: String) {
//        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putString("auth_token", token)
//        editor.apply()
//    }
//
//    // Method to navigate to MainActivity
//    private fun navigateToMainActivity() {
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
//}
