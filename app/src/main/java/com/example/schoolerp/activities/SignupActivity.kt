package com.example.schoolerp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.schoolerp.databinding.ActivitySignupBinding
import com.example.schoolerp.models.User
import com.example.schoolerp.utils.FirebaseHelper

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val userType = when (binding.radioGroup.checkedRadioButtonId) {
                binding.rbStudent.id -> "student"
                binding.rbTeacher.id -> "teacher"
                else -> ""
            }

            if (validateInput(name, email, password, userType)) {
                signupUser(name, email, password, userType)
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateInput(name: String, email: String, password: String, userType: String): Boolean {
        return when {
            name.isEmpty() -> {
                binding.etName.error = "Name is required"
                false
            }
            email.isEmpty() -> {
                binding.etEmail.error = "Email is required"
                false
            }
            password.length < 6 -> {
                binding.etPassword.error = "Password must be at least 6 characters"
                false
            }
            userType.isEmpty() -> {
                Toast.makeText(this, "Please select user type", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun signupUser(name: String, email: String, password: String, userType: String) {
        binding.btnSignup.isEnabled = false

        FirebaseHelper.auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = User(
                    uid = result.user?.uid ?: "",
                    email = email,
                    name = name,
                    userType = userType
                )

                FirebaseHelper.firestore.collection("users")
                    .document(user.uid)
                    .set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        binding.btnSignup.isEnabled = true
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                binding.btnSignup.isEnabled = true
                Toast.makeText(this, "Signup failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}