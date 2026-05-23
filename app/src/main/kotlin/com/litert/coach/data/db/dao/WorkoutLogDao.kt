package com.litert.coach.data.db.dao

import androidx.room.*
import com.litert.coach.data.db.entity.WorkoutLogEntity
import kotlinx.coroutines.flow.Flow

data class HistoricalAverages(
    @ColumnInfo(name = "avgWeeklyDistanceKm") val avgWeeklyDistanceKm: Double,
    @ColumnInfo(name = "avgWeeklyDurationSeconds") val avgWeeklyDurationSeconds: Double,
    @ColumnInfo(name = "avgPaceSecPerKm") val avgPaceSecPerKm: Double,
    @ColumnInfo(name = "avgHeartRate") val avgHeartRate: Double,
    @ColumnInfo(name = "avgPerceivedEffort") val avgPerceivedEffort: Double,
    @ColumnInfo(name = "sessionsPerWeek") val sessionsPerWeek: Double,
    @ColumnInfo(name = "longestDistanceKm") val longestDistanceKm: Double,
    @ColumnInfo(name = "runCount") val runCount: Int,
    @ColumnInfo(name = "cycleCount") val cycleCount: Int
)

@Dao
interface WorkoutLogDao {
    @Query("SELECT * FROM workout_logs ORDER BY logged_at DESC")
    fun observeAll(): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs ORDER BY logged_at DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 7): List<WorkoutLogEntity>

    @Query("SELECT * FROM workout_logs WHERE id = :id")
    suspend fun getById(id: Int): WorkoutLogEntity?

    @Insert
    suspend fun insert(log: WorkoutLogEntity): Long

    @Query("""
        SELECT
            AVG(distance_km) * 7.0 / ((:windowDays) / 7.0) AS avgWeeklyDistanceKm,
            AVG(duration_seconds) * 7.0 / ((:windowDays) / 7.0) AS avgWeeklyDurationSeconds,
            AVG(avg_pace_sec_per_km) AS avgPaceSecPerKm,
            AVG(avg_heart_rate) AS avgHeartRate,
            AVG(perceived_effort) AS avgPerceivedEffort,
            COUNT(*) * 7.0 / :windowDays AS sessionsPerWeek,
            MAX(distance_km) AS longestDistanceKm,
            SUM(CASE WHEN activity_type = 'run' THEN 1 ELSE 0 END) AS runCount,
            SUM(CASE WHEN activity_type = 'cycle' THEN 1 ELSE 0 END) AS cycleCount
        FROM workout_logs
        WHERE logged_at >= :sinceEpochMs
    """)
    suspend fun getHistoricalAverages(sinceEpochMs: Long, windowDays: Int): HistoricalAverages?
}
