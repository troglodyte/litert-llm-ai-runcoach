package com.litert.coach.data.repository

import com.litert.coach.data.db.entity.*
import com.litert.coach.domain.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun UserProfileEntity.toDomain() = UserProfile(
    name = name, age = age, weightKg = weightKg, heightCm = heightCm,
    activityTypes = json.decodeFromString(activityTypes),
    fitnessLevel = fitnessLevel, weeklyAvailabilityDays = weeklyAvailabilityDays,
    currentWeeklyDistanceKm = currentWeeklyDistanceKm,
    targetEventName = targetEventName, targetEventDate = targetEventDate,
    targetEventDistanceKm = targetEventDistanceKm,
    trainingStyle = trainingStyle, maxHeartRate = maxHeartRate,
    benchmarkPaceSecPerKm = benchmarkPaceSecPerKm, preferredUnits = preferredUnits,
    modelVariant = modelVariant, modelDownloaded = modelDownloaded,
    injuriesNotes = injuriesNotes, historyWindowMonths = historyWindowMonths
)

fun UserProfile.toEntity() = UserProfileEntity(
    name = name, age = age, weightKg = weightKg, heightCm = heightCm,
    activityTypes = json.encodeToString<List<String>>(activityTypes),
    fitnessLevel = fitnessLevel, weeklyAvailabilityDays = weeklyAvailabilityDays,
    currentWeeklyDistanceKm = currentWeeklyDistanceKm,
    targetEventName = targetEventName, targetEventDate = targetEventDate,
    targetEventDistanceKm = targetEventDistanceKm,
    trainingStyle = trainingStyle, maxHeartRate = maxHeartRate,
    benchmarkPaceSecPerKm = benchmarkPaceSecPerKm, preferredUnits = preferredUnits,
    modelVariant = modelVariant, modelDownloaded = modelDownloaded,
    injuriesNotes = injuriesNotes, historyWindowMonths = historyWindowMonths
)

fun PlannedWorkoutEntity.toDomain() = PlannedWorkout(
    id = id, planId = planId, dayOfWeek = dayOfWeek,
    activityType = activityType, workoutType = workoutType, description = description,
    targetDistanceKm = targetDistanceKm, targetDurationMin = targetDurationMin,
    intensityZone = intensityZone, targetPaceSecPerKm = targetPaceSecPerKm
)

fun WorkoutLogEntity.toDomain() = WorkoutLog(
    id = id, plannedWorkoutId = plannedWorkoutId, loggedAt = loggedAt,
    activityType = activityType, distanceKm = distanceKm, durationSeconds = durationSeconds,
    avgPaceSecPerKm = avgPaceSecPerKm, avgHeartRate = avgHeartRate, maxHeartRate = maxHeartRate,
    perceivedEffort = perceivedEffort, notes = notes
)

fun WorkoutLog.toEntity() = WorkoutLogEntity(
    plannedWorkoutId = plannedWorkoutId, loggedAt = loggedAt, activityType = activityType,
    distanceKm = distanceKm, durationSeconds = durationSeconds, avgPaceSecPerKm = avgPaceSecPerKm,
    avgHeartRate = avgHeartRate, maxHeartRate = maxHeartRate,
    perceivedEffort = perceivedEffort, notes = notes
)

fun PlannedWorkout.toEntity() = PlannedWorkoutEntity(
    planId = 0, dayOfWeek = dayOfWeek, activityType = activityType,
    workoutType = workoutType, description = description,
    targetDistanceKm = targetDistanceKm, targetDurationMin = targetDurationMin,
    intensityZone = intensityZone, targetPaceSecPerKm = targetPaceSecPerKm
)

fun ChatMessageEntity.toDomain() = ChatMessage(
    id = id, role = role, content = content, isSummarized = isSummarized, createdAt = createdAt
)

fun ChatMessage.toEntity() = ChatMessageEntity(
    role = role, content = content, isSummarized = isSummarized, createdAt = createdAt
)
