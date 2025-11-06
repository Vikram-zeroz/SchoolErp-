package com.example.schoolerp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.schoolerp.databinding.ActivityMainBinding
import com.example.schoolerp.utils.FirebaseHelper
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user is already logged in
        if (FirebaseHelper.isLoggedIn()) {
            checkUserTypeAndNavigate()
        } else {
            setupButtons()
        }
    }

    private fun setupButtons() {
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
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
                    else -> return@addOnSuccessListener
                }
                startActivity(intent)
                finish()
            }
    }
}