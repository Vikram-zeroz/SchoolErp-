package com.example.schoolerp.models

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val userType: String = "", // "student" or "teacher"
    val createdAt: Long = System.currentTimeMillis()
)