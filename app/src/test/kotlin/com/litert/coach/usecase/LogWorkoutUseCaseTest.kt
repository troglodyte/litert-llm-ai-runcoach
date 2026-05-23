package com.litert.coach.usecase

import com.litert.coach.domain.model.WorkoutLog
import com.litert.coach.domain.repository.WorkoutRepository
import com.litert.coach.domain.usecase.LogWorkoutUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class LogWorkoutUseCaseTest {
    private val repo = mockk<WorkoutRepository>()
    private val useCase = LogWorkoutUseCase(repo)

    @Test
    fun calculates_pace_and_inserts() = runTest {
        coEvery { repo.insert(any()) } returns 1L
        val log = WorkoutLog(
            id = 0, loggedAt = 1000L, activityType = "run",
            distanceKm = 5f, durationSeconds = 1500, perceivedEffort = 7
        )
        useCase(log)
        coVerify {
            repo.insert(withArg { saved ->
                assertEquals(300, saved.avgPaceSecPerKm) // 1500s / 5km = 300s/km
            })
        }
    }

    @Test
    fun pace_is_null_when_no_distance() = runTest {
        coEvery { repo.insert(any()) } returns 1L
        val log = WorkoutLog(
            id = 0, loggedAt = 1000L, activityType = "cycle",
            distanceKm = null, durationSeconds = 3600, perceivedEffort = 5
        )
        useCase(log)
        coVerify { repo.insert(withArg { assertNull(it.avgPaceSecPerKm) }) }
    }
}
