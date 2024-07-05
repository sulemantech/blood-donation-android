package com.welfare.blood.donation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.welfare.blood.donation.databinding.ActivityLoginBinding
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Check if user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToHome()
            return // Exit onCreate if user is already logged in
        }

        binding.btnLogin.setOnClickListener {
            loginUser()
        }
        binding.register.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.reset.setOnClickListener{
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser() {
        val email = binding.edUsername.text.toString().trim()
        val password = binding.edPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    val userID = auth.currentUser!!.uid
                    val lastLoginAt = Calendar.getInstance().time

                    val updateData = hashMapOf("lastLoginAt" to lastLoginAt)

                    db.collection("users").document(userID).set(updateData, SetOptions.merge())
                        .addOnSuccessListener {
                            binding.progressBar.visibility = View.GONE
                            navigateToHome()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error updating document", e)
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, "Error updating login timestamp", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}



//        binding.forgotPassword.setOnClickListener {
//            val builder = AlertDialog.Builder(this)
//            val view = layoutInflater.inflate(R.layout.dialog_forgot, null)
//            val userEmail = view.findViewById<EditText>(R.id.editBox)
//
//            builder.setView(view)
//            val dialog = builder.create()
//
//            view.findViewById<Button>(R.id.btnReset).setOnClickListener {
//                compareEmail(userEmail)
//                dialog.dismiss()
//            }
//            view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
//                dialog.dismiss()
//            }
//            if (dialog.window != null){
//                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
//            }
//            dialog.show()
//        }



    //Outside onCreate
//    private fun compareEmail(email: EditText){
//        if (email.text.toString().isEmpty()){
//            return
//        }
//        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
//            return
//        }
//        firebaseAuth.sendPasswordResetEmail(email.text.toString())
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//    override fun onStart() {
//        super.onStart()
//
//        if (auth.currentUser != null) {
//            val intent = Intent(this, HomeActivity::class.java)
//            startActivity(intent)
//        }
//    }



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
