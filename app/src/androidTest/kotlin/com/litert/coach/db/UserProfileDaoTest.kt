package com.litert.coach.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.litert.coach.data.db.AppDatabase
import com.litert.coach.data.db.entity.UserProfileEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserProfileDaoTest {
    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun teardown() = db.close()

    @Test
    fun upsert_and_observe_returns_profile() = runTest {
        val profile = UserProfileEntity(
            name = "Ada", age = 32, weightKg = 65f, heightCm = 170f,
            activityTypes = "[\"running\"]", fitnessLevel = "intermediate",
            weeklyAvailabilityDays = 4, currentWeeklyDistanceKm = 30f,
            trainingStyle = "balanced"
        )
        db.userProfileDao().upsert(profile)
        val result = db.userProfileDao().observe().first()
        assertNotNull(result)
        assertEquals("Ada", result!!.name)
    }

    @Test
    fun updateModelDownloaded_sets_flag_true() = runTest {
        db.userProfileDao().upsert(UserProfileEntity(
            name = "Ada", age = 32, weightKg = 65f, heightCm = 170f,
            activityTypes = "[\"running\"]", fitnessLevel = "intermediate",
            weeklyAvailabilityDays = 4, currentWeeklyDistanceKm = 30f,
            trainingStyle = "balanced"
        ))
        db.userProfileDao().updateModelDownloaded(true)
        assertTrue(db.userProfileDao().get()!!.modelDownloaded)
    }
}
