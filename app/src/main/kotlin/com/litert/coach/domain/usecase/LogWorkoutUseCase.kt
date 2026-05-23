package com.litert.coach.domain.usecase

import com.litert.coach.domain.model.WorkoutLog
import com.litert.coach.domain.repository.WorkoutRepository
import javax.inject.Inject

class LogWorkoutUseCase @Inject constructor(private val repo: WorkoutRepository) {
    suspend operator fun invoke(log: WorkoutLog): Long {
        val pace = if (log.distanceKm != null && log.distanceKm > 0)
            (log.durationSeconds / log.distanceKm).toInt() else null
        return repo.insert(log.copy(avgPaceSecPerKm = pace))
    }
}
