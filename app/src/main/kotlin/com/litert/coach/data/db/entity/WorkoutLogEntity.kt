package com.litert.coach.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_logs",
    foreignKeys = [ForeignKey(
        entity = PlannedWorkoutEntity::class,
        parentColumns = ["id"],
        childColumns = ["planned_workout_id"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("planned_workout_id"), Index("logged_at")]
)
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "planned_workout_id") val plannedWorkoutId: Int? = null,
    @ColumnInfo(name = "logged_at") val loggedAt: Long,
    @ColumnInfo(name = "activity_type") val activityType: String,
    @ColumnInfo(name = "distance_km") val distanceKm: Float? = null,
    @ColumnInfo(name = "duration_seconds") val durationSeconds: Int,
    @ColumnInfo(name = "avg_pace_sec_per_km") val avgPaceSecPerKm: Int? = null,
    @ColumnInfo(name = "avg_heart_rate") val avgHeartRate: Int? = null,
    @ColumnInfo(name = "max_heart_rate") val maxHeartRate: Int? = null,
    @ColumnInfo(name = "perceived_effort") val perceivedEffort: Int,  // 1-10
    val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
