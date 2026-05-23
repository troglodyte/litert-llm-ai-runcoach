package com.litert.coach.usecase

import com.litert.coach.data.db.dao.HistoricalAverages
import com.litert.coach.domain.repository.ProfileRepository
import com.litert.coach.domain.repository.WorkoutRepository
import com.litert.coach.domain.usecase.GetHistoricalAveragesUseCase
import com.litert.coach.domain.model.UserProfile
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class GetHistoricalAveragesUseCaseTest {

    private val profileRepo = mockk<ProfileRepository>()
    private val workoutRepo = mockk<WorkoutRepository>()
    private val useCase = GetHistoricalAveragesUseCase(profileRepo, workoutRepo)

    private fun profile(months: Int = 3) = UserProfile(
        name = "Ada", age = 30, weightKg = 60f, heightCm = 165f,
        activityTypes = listOf("running"), fitnessLevel = "intermediate",
        weeklyAvailabilityDays = 4, currentWeeklyDistanceKm = 25f,
        trainingStyle = "balanced", historyWindowMonths = months
    )

    @Test
    fun returns_null_when_no_profile() = runTest {
        coEvery { profileRepo.get() } returns null
        assertNull(useCase())
    }

    @Test
    fun returns_averages_from_repo() = runTest {
        val avg = HistoricalAverages(40.0, 14400.0, 320.0, 155.0, 6.5, 4.0, 15.0, 12, 0)
        coEvery { profileRepo.get() } returns profile(3)
        coEvery { workoutRepo.getHistoricalAverages(3) } returns avg
        assertEquals(avg, useCase())
    }
}
