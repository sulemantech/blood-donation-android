package com.welfare.blood.donation

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.rpc.context.AttributeContext.Auth
import com.welfare.blood.donation.databinding.ActivityLoginBinding
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToHome()
            return
        }

        binding.btnLogin.setOnClickListener {
            loginUser()
        }
        binding.checkBoxShowPassword.setOnCheckedChangeListener { _, isChecked ->
            isPasswordVisible = isChecked
            togglePasswordVisibility()
        }
        binding.register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.reset.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            val view =layoutInflater.inflate(R.layout.forget_password,null)
            val userEmail = view.findViewById<EditText>(R.id.editBox)

            builder.setView(view)
            val dialog = builder.create()

            view.findViewById<Button>(R.id.reset).setOnClickListener{
                compareEmail(userEmail)
                dialog.dismiss()
            }
            view.findViewById<Button>(R.id.cancel).setOnClickListener{
                dialog.dismiss()
            }
            if (dialog.window != null)
                dialog.window!!.setBackgroundDrawable(ColorDrawable (0))
            dialog.show()
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

    private fun compareEmail(email: EditText) {
        val emailText = email.text.toString().trim()

        if (emailText.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(emailText).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Check your email for password reset instructions", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error sending password reset email", Toast.LENGTH_SHORT).show()
            }
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

                            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putBoolean("isLoggedIn", true)
                                apply()
                            }
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

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.edPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            binding.edPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        binding.edPassword.setSelection(binding.edPassword.text.length)
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