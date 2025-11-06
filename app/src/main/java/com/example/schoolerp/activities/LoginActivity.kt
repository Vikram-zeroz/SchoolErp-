package com.example.schoolerp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.schoolerp.databinding.ActivityLoginBinding
import com.example.schoolerp.utils.FirebaseHelper
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                loginUser(email, password)
            }
        }

        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.etEmail.error = "Email is required"
                false
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Password is required"
                false
            }
            else -> true
        }
    }

    private fun loginUser(email: String, password: String) {
        binding.btnLogin.isEnabled = false

        FirebaseHelper.auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkUserTypeAndNavigate()
            }
            .addOnFailureListener { e ->
                binding.btnLogin.isEnabled = true
                Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUserTypeAndNavigate() {
        val userId = FirebaseHelper.getCurrentUser()?.uid ?: return

        FirebaseFirestore.getInstance().collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val userType = document.getString("userType")
                val intent = when (userType) {
                    "student" -> Intent(this, StudentDashboardActivity::class.java)
                    "teacher" -> Intent(this, TeacherDashboardActivity::class.java)
                    else -> {
                        Toast.makeText(this, "Invalid user type", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                }
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show()
            }
    }
}