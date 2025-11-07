package com.example.schoolerp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.schoolerp.databinding.ActivityStudentProfileBinding
import com.example.schoolerp.models.User
import com.example.schoolerp.utils.FirebaseHelper
import com.google.firebase.firestore.FirebaseFirestore

class StudentProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentProfileBinding
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadUserProfile()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Profile"
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadUserProfile() {
        val userId = FirebaseHelper.getCurrentUser()?.uid ?: return

        binding.progressBar.visibility = android.view.View.VISIBLE

        FirebaseFirestore.getInstance().collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                binding.progressBar.visibility = android.view.View.GONE
                currentUser = document.toObject(User::class.java)
                displayUserInfo()
            }
            .addOnFailureListener {
                binding.progressBar.visibility = android.view.View.GONE
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayUserInfo() {
        currentUser?.let { user ->
            binding.apply {
                tvName.text = user.name
                tvEmail.text = user.email
                tvRollNumber.text = "Roll No: ${user.rollNumber.ifEmpty { "Not Set" }}"
                tvClass.text = "Class: ${user.className.ifEmpty { "Not Set" }}"
                tvSection.text = "Section: ${user.section.ifEmpty { "Not Set" }}"

                etName.setText(user.name)
                etRollNumber.setText(user.rollNumber)
                etClassName.setText(user.className)
                etSection.setText(user.section)
                etPhone.setText(user.phoneNumber)
                etAddress.setText(user.address)
                etParentName.setText(user.parentName)
                etParentPhone.setText(user.parentPhone)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSaveProfile.setOnClickListener {
            saveProfile()
        }

        binding.btnEditProfile.setOnClickListener {
            toggleEditMode(true)
        }

        binding.btnCancelEdit.setOnClickListener {
            toggleEditMode(false)
            displayUserInfo()
        }
    }

    private fun toggleEditMode(isEditing: Boolean) {
        binding.apply {
            etName.isEnabled = isEditing
            etRollNumber.isEnabled = isEditing
            etClassName.isEnabled = isEditing
            etSection.isEnabled = isEditing
            etPhone.isEnabled = isEditing
            etAddress.isEnabled = isEditing
            etParentName.isEnabled = isEditing
            etParentPhone.isEnabled = isEditing

            btnSaveProfile.visibility = if (isEditing) android.view.View.VISIBLE else android.view.View.GONE
            btnCancelEdit.visibility = if (isEditing) android.view.View.VISIBLE else android.view.View.GONE
            btnEditProfile.visibility = if (isEditing) android.view.View.GONE else android.view.View.VISIBLE
        }
    }

    private fun saveProfile() {
        val userId = FirebaseHelper.getCurrentUser()?.uid ?: return

        val updatedUser = hashMapOf(
            "name" to binding.etName.text.toString().trim(),
            "rollNumber" to binding.etRollNumber.text.toString().trim(),
            "className" to binding.etClassName.text.toString().trim(),
            "section" to binding.etSection.text.toString().trim(),
            "phoneNumber" to binding.etPhone.text.toString().trim(),
            "address" to binding.etAddress.text.toString().trim(),
            "parentName" to binding.etParentName.text.toString().trim(),
            "parentPhone" to binding.etParentPhone.text.toString().trim()
        )

        binding.progressBar.visibility = android.view.View.VISIBLE

        FirebaseFirestore.getInstance().collection("users")
            .document(userId)
            .update(updatedUser as Map<String, Any>)
            .addOnSuccessListener {
                binding.progressBar.visibility = android.view.View.GONE
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                toggleEditMode(false)
                loadUserProfile()
            }
            .addOnFailureListener {
                binding.progressBar.visibility = android.view.View.GONE
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }
}