package com.litert.coach.domain.model

data class UserProfile(
    val name: String, val age: Int, val weightKg: Float, val heightCm: Float,
    val activityTypes: List<String>,
    val fitnessLevel: String,
    val weeklyAvailabilityDays: Int,
    val currentWeeklyDistanceKm: Float,
    val targetEventName: String? = null,
    val targetEventDate: Long? = null,
    val targetEventDistanceKm: Float? = null,
    val trainingStyle: String,
    val maxHeartRate: Int? = null,
    val benchmarkPaceSecPerKm: Int? = null,
    val preferredUnits: String = "metric",
    val modelVariant: String = "1B",
    val modelDownloaded: Boolean = false,
    val injuriesNotes: String? = null,
    val historyWindowMonths: Int = 3
)
