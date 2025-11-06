package com.example.schoolerp.models

data class Note(
    val id: String = "",
    val title: String = "",
    val subject: String = "",
    val fileUrl: String = "",
    val fileName: String = "",
    val uploadedBy: String = "",
    val uploadedByName: String = ""
)