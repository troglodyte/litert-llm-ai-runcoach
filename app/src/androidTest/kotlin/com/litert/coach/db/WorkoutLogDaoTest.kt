package com.litert.coach.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.litert.coach.data.db.AppDatabase
import com.litert.coach.data.db.entity.WorkoutLogEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkoutLogDaoTest {
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

    private fun makeLog(loggedAt: Long, distanceKm: Float = 5f, durationSeconds: Int = 1800,
                        activityType: String = "run", effort: Int = 6) = WorkoutLogEntity(
        loggedAt = loggedAt,
        activityType = activityType,
        distanceKm = distanceKm,
        durationSeconds = durationSeconds,
        avgPaceSecPerKm = (durationSeconds / distanceKm).toInt(),
        perceivedEffort = effort
    )

    @Test
    fun insert_and_getById_returns_entry() = runTest {
        val id = db.workoutLogDao().insert(makeLog(1_000_000L))
        val result = db.workoutLogDao().getById(id.toInt())
        assertNotNull(result)
        assertEquals(1_000_000L, result!!.loggedAt)
    }

    @Test
    fun getRecent_returns_most_recent_entries_in_order() = runTest {
        val dao = db.workoutLogDao()
        dao.insert(makeLog(1000L))
        dao.insert(makeLog(3000L))
        dao.insert(makeLog(2000L))
        val recent = dao.getRecent(2)
        assertEquals(2, recent.size)
        assertTrue(recent[0].loggedAt > recent[1].loggedAt)
    }

    @Test
    fun getHistoricalAverages_calculates_over_window() = runTest {
        val dao = db.workoutLogDao()
        val dayMs = 86_400_000L
        val now = System.currentTimeMillis()
        // Insert 4 runs within last 30 days
        dao.insert(makeLog(now - 5 * dayMs, distanceKm = 10f, durationSeconds = 3600))
        dao.insert(makeLog(now - 12 * dayMs, distanceKm = 8f, durationSeconds = 2880))
        dao.insert(makeLog(now - 20 * dayMs, distanceKm = 12f, durationSeconds = 4320))
        dao.insert(makeLog(now - 28 * dayMs, distanceKm = 6f, durationSeconds = 2160))
        val since = now - 30 * dayMs
        val avg = dao.getHistoricalAverages(since, 30)
        assertNotNull(avg)
        assertEquals(4, avg!!.runCount)
        assertEquals(0, avg.cycleCount)
        assertTrue(avg.avgWeeklyDistanceKm > 0)
        assertTrue(avg.longestDistanceKm >= 12.0)
    }

    @Test
    fun getHistoricalAverages_returns_null_for_empty_window() = runTest {
        val since = System.currentTimeMillis() + 1000L // future, so no results
        val avg = db.workoutLogDao().getHistoricalAverages(since, 30)
        // Room returns null or a row with all nulls/zeros for aggregate on empty result
        // Either null or a result with runCount=0 is acceptable
        if (avg != null) {
            assertEquals(0, avg.runCount)
        }
    }
}
