package com.litert.coach.data.repository

import com.litert.coach.data.db.dao.PlanDao
import com.litert.coach.data.db.entity.TrainingPlanEntity
import com.litert.coach.domain.model.PlannedWorkout
import com.litert.coach.domain.model.TrainingPlan
import com.litert.coach.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlanRepositoryImpl @Inject constructor(private val dao: PlanDao) : PlanRepository {

    override fun observeActivePlan(): Flow<TrainingPlan?> =
        dao.observeActivePlan().flatMapLatest { plan ->
            if (plan == null) flowOf(null)
            else dao.observeWorkoutsForPlan(plan.id).map { workouts ->
                TrainingPlan(
                    id = plan.id, weekStartDate = plan.weekStartDate,
                    weekEndDate = plan.weekEndDate, aiInsight = plan.aiInsight,
                    workouts = workouts.map { it.toDomain() }
                )
            }
        }

    override suspend fun getActivePlan(): TrainingPlan? {
        val plan = dao.getActivePlan() ?: return null
        val workouts = dao.getWorkoutsForPlan(plan.id)
        return TrainingPlan(
            id = plan.id, weekStartDate = plan.weekStartDate,
            weekEndDate = plan.weekEndDate, aiInsight = plan.aiInsight,
            workouts = workouts.map { it.toDomain() }
        )
    }

    override suspend fun getWorkout(id: Int): PlannedWorkout? = dao.getWorkout(id)?.toDomain()

    override suspend fun replacePlan(plan: TrainingPlan) {
        val entity = TrainingPlanEntity(
            weekStartDate = plan.weekStartDate, weekEndDate = plan.weekEndDate,
            aiInsight = plan.aiInsight
        )
        val workoutEntities = plan.workouts.map { it.toEntity() }
        dao.replacePlan(entity, workoutEntities)
    }
}
