package com.example.schoolerp.models

data class Note(
    val id: String = "",
    val title: String = "",

    val description: String = "",
    val fileUrl: String = "",
    val date: String = "",
    val uploadedBy: String = "",
    val uploadedByName: String = ""
)