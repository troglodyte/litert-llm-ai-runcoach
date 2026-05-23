package com.litert.coach.domain.repository

import com.litert.coach.domain.model.PlannedWorkout
import com.litert.coach.domain.model.TrainingPlan
import kotlinx.coroutines.flow.Flow

interface PlanRepository {
    fun observeActivePlan(): Flow<TrainingPlan?>
    suspend fun getActivePlan(): TrainingPlan?
    suspend fun getWorkout(id: Int): PlannedWorkout?
    suspend fun replacePlan(plan: TrainingPlan)
}
