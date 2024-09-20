package com.welfare.blood.donation

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.welfare.blood.donation.databinding.ActivityLoginBinding
import java.util.*
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private var isPasswordVisible = false
    private val RC_SIGN_IN = 9001
    private var isLoginInProgress = false
    private lateinit var errorMessageTextView: TextView
    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        errorMessageTextView = findViewById(R.id.errorMessageTextView)

        // Loading Dialog Setup
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_loader, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        loadingDialog = builder.create()

        // TextWatcher to hide error message on text change
        binding.edUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                errorMessageTextView.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                errorMessageTextView.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Set up Google sign-in
        setupGoogleSignIn()

        // Underline text for "Sign Up" and "Reset"
        val mTextView = findViewById<TextView>(R.id.register)
        val mTextView1 = findViewById<TextView>(R.id.reset)
        val mString = "Sign Up"
        val mString1 = "Reset"
        val mSpannableString = SpannableString(mString)
        val mSpannableString1 = SpannableString(mString1)
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
        mSpannableString1.setSpan(UnderlineSpan(), 0, mSpannableString1.length, 0)
        mTextView.text = mSpannableString
        mTextView1.text = mSpannableString1

        // Status bar customization
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

        // Login button listener
        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        // Show password toggle
        binding.checkBoxShowPassword.setOnCheckedChangeListener { _, isChecked ->
            isPasswordVisible = isChecked
            togglePasswordVisibility()
        }

        // Sign up navigation
        binding.register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Reset password dialog
        binding.reset.setOnClickListener {
            showResetPasswordDialog()
        }

        // Google sign-in button
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
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

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("YOUR_GOOGLE_CLIENT_ID")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                showError("Google sign-in failed")
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userID = auth.currentUser!!.uid
                    val lastLoginAt = Calendar.getInstance().time
                    val updateData = hashMapOf("lastLoginAt" to lastLoginAt)
                    db.collection("users").document(userID).set(updateData, SetOptions.merge())
                        .addOnSuccessListener {
                            navigateToHome()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error updating document", e)
                            showError("Error updating login timestamp")
                        }
                } else {
                    showError("Google sign-in failed")
                }
            }
    }

    private fun loginUser() {
        if (isLoginInProgress) {
            Toast.makeText(this, "Login in progress, please wait...", Toast.LENGTH_SHORT).show()
            return
        }

        val email = binding.edUsername.text.toString().trim()
        val password = binding.edPassword.text.toString().trim()

        if (!isValidEmail(email)) {
            showError("Please enter a valid Email")
            return
        }

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter Email and Password")
            return
        }

        loadingDialog.show()
        isLoginInProgress = true
        binding.btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                isLoginInProgress = false
                binding.btnLogin.isEnabled = true
                loadingDialog.dismiss()

                if (task.isSuccessful) {
                    val userID = auth.currentUser!!.uid
                    val lastLoginAt = Calendar.getInstance().time
                    val updateData = hashMapOf("lastLoginAt" to lastLoginAt)
                    db.collection("users").document(userID).set(updateData, SetOptions.merge())
                        .addOnSuccessListener {
                            navigateToHome()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error updating document", e)
                            showError("Error updating login timestamp")
                        }
                } else {
                    showError("Incorrect Email or Password. Please try again.")
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

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showResetPasswordDialog() {
        // Create AlertDialog.Builder to show custom dialog
        val builder = AlertDialog.Builder(this)

        // Inflate the custom dialog layout 'forget_password.xml'
        val dialogView = layoutInflater.inflate(R.layout.forget_password, null)

        // Set the custom view for the AlertDialog
        builder.setView(dialogView)

        // Create the AlertDialog
        val alertDialog = builder.create()

        // Find the EditText where the user will enter the email
        val emailEditText = dialogView.findViewById<EditText>(R.id.editBox)

        // Find custom buttons in your custom layout
        val sendButton = dialogView.findViewById<Button>(R.id.reset) // Assuming you have a button with id 'btnSend'
        val cancelButton = dialogView.findViewById<Button>(R.id.cancel) // Assuming you have a button with id 'btnCancel'

        // Handle the Send button click
        sendButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (isValidEmail(email)) {
                sendResetPasswordEmail(email)
            } else {
                showError("Please enter a valid email address")
            }
            alertDialog.dismiss() // Close the dialog after action
        }

        // Handle the Cancel button click
        cancelButton.setOnClickListener {
            alertDialog.dismiss() // Close the dialog when cancel is clicked
        }

        // Show the custom AlertDialog
        alertDialog.show()
    }

    private fun sendResetPasswordEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset Email sent.", Toast.LENGTH_SHORT).show()
                } else {
                    showError("Failed to send Password reset Email.")
                }
            }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showError(message: String) {
        errorMessageTextView.text = message
        errorMessageTextView.visibility = View.VISIBLE
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
