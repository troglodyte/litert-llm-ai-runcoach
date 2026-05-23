package com.litert.coach.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.litert.coach.data.db.dao.*
import com.litert.coach.data.db.entity.*

@Database(
    entities = [
        UserProfileEntity::class,
        TrainingPlanEntity::class,
        PlannedWorkoutEntity::class,
        WorkoutLogEntity::class,
        ChatMessageEntity::class,
        AiContextSummaryEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun planDao(): PlanDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun chatDao(): ChatDao
}
