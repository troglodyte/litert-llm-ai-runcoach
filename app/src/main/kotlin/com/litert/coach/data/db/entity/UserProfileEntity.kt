package com.litert.coach.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val age: Int,
    @ColumnInfo(name = "weight_kg") val weightKg: Float,
    @ColumnInfo(name = "height_cm") val heightCm: Float,
    @ColumnInfo(name = "activity_types") val activityTypes: String, // JSON: ["running","cycling"]
    @ColumnInfo(name = "fitness_level") val fitnessLevel: String,   // beginner/intermediate/advanced
    @ColumnInfo(name = "weekly_availability_days") val weeklyAvailabilityDays: Int,
    @ColumnInfo(name = "current_weekly_distance_km") val currentWeeklyDistanceKm: Float,
    @ColumnInfo(name = "target_event_name") val targetEventName: String? = null,
    @ColumnInfo(name = "target_event_date") val targetEventDate: Long? = null,
    @ColumnInfo(name = "target_event_distance_km") val targetEventDistanceKm: Float? = null,
    @ColumnInfo(name = "training_style") val trainingStyle: String, // high_mileage/intensity_based/balanced
    @ColumnInfo(name = "max_heart_rate") val maxHeartRate: Int? = null,
    @ColumnInfo(name = "benchmark_pace_sec_per_km") val benchmarkPaceSecPerKm: Int? = null,
    @ColumnInfo(name = "preferred_units") val preferredUnits: String = "metric",
    @ColumnInfo(name = "model_variant") val modelVariant: String = "1B",
    @ColumnInfo(name = "model_downloaded") val modelDownloaded: Boolean = false,
    @ColumnInfo(name = "injuries_notes") val injuriesNotes: String? = null,
    @ColumnInfo(name = "history_window_months") val historyWindowMonths: Int = 3,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)
