package com.litert.coach.domain.model

data class WorkoutLog(
    val id: Int, val plannedWorkoutId: Int? = null,
    val loggedAt: Long, val activityType: String,
    val distanceKm: Float? = null, val durationSeconds: Int,
    val avgPaceSecPerKm: Int? = null,
    val avgHeartRate: Int? = null, val maxHeartRate: Int? = null,
    val perceivedEffort: Int, val notes: String? = null
)
