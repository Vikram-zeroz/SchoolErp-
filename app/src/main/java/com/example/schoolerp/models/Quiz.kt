package com.example.schoolerp.models

data class Quiz(
    val id: String = "",
    val title: String = "",
    val subject: String = "",
    val questions: List<Question> = listOf(),
    val createdBy: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Question(
    val question: String = "",
    val options: List<String> = listOf(),
    val correctAnswer: Int = 0
)

data class QuizResult(
    val quizId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)