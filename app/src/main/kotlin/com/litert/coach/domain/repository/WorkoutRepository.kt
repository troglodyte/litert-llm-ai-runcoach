package com.litert.coach.domain.repository

import com.litert.coach.data.db.dao.HistoricalAverages
import com.litert.coach.domain.model.WorkoutLog
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun observeAll(): Flow<List<WorkoutLog>>
    suspend fun getRecent(limit: Int = 7): List<WorkoutLog>
    suspend fun getById(id: Int): WorkoutLog?
    suspend fun insert(log: WorkoutLog): Long
    suspend fun getHistoricalAverages(windowMonths: Int): HistoricalAverages?
}
