package com.litert.coach.data.db.dao

import androidx.room.*
import com.litert.coach.data.db.entity.PlannedWorkoutEntity
import com.litert.coach.data.db.entity.TrainingPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Query("SELECT * FROM training_plans WHERE is_active = 1 LIMIT 1")
    fun observeActivePlan(): Flow<TrainingPlanEntity?>

    @Query("SELECT * FROM training_plans WHERE is_active = 1 LIMIT 1")
    suspend fun getActivePlan(): TrainingPlanEntity?

    @Query("SELECT * FROM planned_workouts WHERE plan_id = :planId ORDER BY day_of_week, sort_order")
    fun observeWorkoutsForPlan(planId: Int): Flow<List<PlannedWorkoutEntity>>

    @Query("SELECT * FROM planned_workouts WHERE plan_id = :planId ORDER BY day_of_week, sort_order")
    suspend fun getWorkoutsForPlan(planId: Int): List<PlannedWorkoutEntity>

    @Query("SELECT * FROM planned_workouts WHERE id = :id")
    suspend fun getWorkout(id: Int): PlannedWorkoutEntity?

    @Insert
    suspend fun insertPlan(plan: TrainingPlanEntity): Long

    @Insert
    suspend fun insertWorkouts(workouts: List<PlannedWorkoutEntity>)

    @Query("UPDATE training_plans SET is_active = 0 WHERE is_active = 1")
    suspend fun deactivateAllPlans()

    @Transaction
    suspend fun replacePlan(plan: TrainingPlanEntity, workouts: List<PlannedWorkoutEntity>) {
        deactivateAllPlans()
        val newId = insertPlan(plan)
        insertWorkouts(workouts.map { it.copy(planId = newId.toInt()) })
    }
}
