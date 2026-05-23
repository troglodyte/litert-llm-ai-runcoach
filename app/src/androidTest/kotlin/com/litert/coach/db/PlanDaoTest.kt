package com.litert.coach.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.litert.coach.data.db.AppDatabase
import com.litert.coach.data.db.entity.PlannedWorkoutEntity
import com.litert.coach.data.db.entity.TrainingPlanEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlanDaoTest {
    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun teardown() = db.close()

    private fun makePlan() = TrainingPlanEntity(
        weekStartDate = 1_000_000L, weekEndDate = 1_604_800L,
        aiInsight = "Great week ahead!", isActive = true
    )

    private fun makeWorkout(planId: Int, day: Int) = PlannedWorkoutEntity(
        planId = planId, dayOfWeek = day, activityType = "run",
        workoutType = "easy"
    )

    @Test
    fun replacePlan_deactivates_old_plan_and_inserts_new() = runTest {
        val dao = db.planDao()
        dao.replacePlan(makePlan(), listOf(makeWorkout(0, 1), makeWorkout(0, 3)))
        val first = dao.getActivePlan()
        assertNotNull(first)

        dao.replacePlan(makePlan().copy(aiInsight = "Week 2"), listOf(makeWorkout(0, 2)))
        val active = dao.getActivePlan()
        assertEquals("Week 2", active!!.aiInsight)
        val workouts = dao.getWorkoutsForPlan(active.id)
        assertEquals(1, workouts.size)
        assertEquals(2, workouts[0].dayOfWeek)
    }
}
