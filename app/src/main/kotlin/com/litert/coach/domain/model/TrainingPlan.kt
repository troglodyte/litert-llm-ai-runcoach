package com.litert.coach.domain.model

data class TrainingPlan(
    val id: Int, val weekStartDate: Long, val weekEndDate: Long,
    val aiInsight: String, val workouts: List<PlannedWorkout>
)

data class PlannedWorkout(
    val id: Int, val planId: Int, val dayOfWeek: Int,
    val activityType: String, val workoutType: String, val description: String?,
    val targetDistanceKm: Float? = null, val targetDurationMin: Int? = null,
    val intensityZone: String? = null, val targetPaceSecPerKm: Int? = null
)
