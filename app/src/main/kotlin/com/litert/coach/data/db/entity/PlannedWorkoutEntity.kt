package com.litert.coach.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "planned_workouts",
    foreignKeys = [ForeignKey(
        entity = TrainingPlanEntity::class,
        parentColumns = ["id"],
        childColumns = ["plan_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["plan_id", "day_of_week", "sort_order"])]
)
data class PlannedWorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "plan_id") val planId: Int,
    @ColumnInfo(name = "day_of_week") val dayOfWeek: Int,  // 1=Mon, 7=Sun
    @ColumnInfo(name = "activity_type") val activityType: String, // run/cycle/rest
    @ColumnInfo(name = "workout_type") val workoutType: String,   // easy/interval/long/tempo/rest
    val description: String? = null,
    @ColumnInfo(name = "target_distance_km") val targetDistanceKm: Float? = null,
    @ColumnInfo(name = "target_duration_min") val targetDurationMin: Int? = null,
    @ColumnInfo(name = "intensity_zone") val intensityZone: String? = null,
    @ColumnInfo(name = "target_pace_sec_per_km") val targetPaceSecPerKm: Int? = null,
    @ColumnInfo(name = "sort_order") val sortOrder: Int = 0
)
