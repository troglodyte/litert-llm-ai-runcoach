package com.litert.coach.domain.usecase

import com.litert.coach.data.db.dao.HistoricalAverages
import com.litert.coach.domain.repository.ProfileRepository
import com.litert.coach.domain.repository.WorkoutRepository
import javax.inject.Inject

class GetHistoricalAveragesUseCase @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val workoutRepo: WorkoutRepository
) {
    suspend operator fun invoke(): HistoricalAverages? {
        val profile = profileRepo.get() ?: return null
        return workoutRepo.getHistoricalAverages(profile.historyWindowMonths)
    }
}
