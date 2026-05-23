package com.litert.coach.data.repository

import com.litert.coach.data.db.dao.HistoricalAverages
import com.litert.coach.data.db.dao.WorkoutLogDao
import com.litert.coach.domain.model.WorkoutLog
import com.litert.coach.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(private val dao: WorkoutLogDao) : WorkoutRepository {
    override fun observeAll(): Flow<List<WorkoutLog>> = dao.observeAll().map { list -> list.map { it.toDomain() } }
    override suspend fun getRecent(limit: Int) = dao.getRecent(limit).map { it.toDomain() }
    override suspend fun getById(id: Int) = dao.getById(id)?.toDomain()
    override suspend fun insert(log: WorkoutLog) = dao.insert(log.toEntity())
    override suspend fun getHistoricalAverages(windowMonths: Int): HistoricalAverages? {
        val windowDays = windowMonths * 30
        val sinceMs = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(windowDays.toLong())
        return dao.getHistoricalAverages(sinceMs, windowDays)
    }
}
