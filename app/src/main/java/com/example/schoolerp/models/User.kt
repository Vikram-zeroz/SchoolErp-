package com.example.schoolerp.models

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val userType: String = "",
    val rollNumber: String = "",
    val className: String = "",
    val section: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val parentName: String = "",
    val parentPhone: String = ""
)