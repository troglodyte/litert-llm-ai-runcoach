package com.litert.coach.domain.usecase

import com.litert.coach.ai.CoachModel
import com.litert.coach.ai.ModelVariant
import com.litert.coach.ai.PromptBuilder
import com.litert.coach.domain.model.PlannedWorkout
import com.litert.coach.domain.model.TrainingPlan
import com.litert.coach.domain.repository.PlanRepository
import com.litert.coach.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.*
import java.util.*
import javax.inject.Inject

class GeneratePlanUseCase @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val planRepo: PlanRepository,
    private val model: CoachModel,
    private val promptBuilder: PromptBuilder,
    private val historicalAverages: GetHistoricalAveragesUseCase
) {
    suspend operator fun invoke() {
        val profile = profileRepo.get() ?: return
        if (!model.isReady()) {
            runCatching {
                model.setVariant(ModelVariant.valueOf(profile.modelVariant))
                model.load()
            }
            if (!model.isReady()) return
        }

        val averages = historicalAverages()
        val prompt = promptBuilder.buildPlanGenerationPrompt(profile, averages)

        val rawJson = model.generate(prompt).toList().joinToString("")
        val plan = parsePlan(rawJson) ?: run {
            val retryPrompt = "$prompt\n\nIMPORTANT: Return ONLY the JSON array, nothing else."
            val retryJson = model.generate(retryPrompt).toList().joinToString("")
            parsePlan(retryJson) ?: return
        }
        planRepo.replacePlan(plan)
    }

    private fun parsePlan(raw: String): TrainingPlan? = runCatching {
        val cleaned = raw.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val array = Json.parseToJsonElement(cleaned).jsonArray
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val weekStart = cal.timeInMillis
        val weekEnd = weekStart + 6 * 24 * 60 * 60 * 1000L

        val workouts = array.mapIndexed { idx, el ->
            val obj = el.jsonObject
            PlannedWorkout(
                id = 0, planId = 0,
                dayOfWeek = obj["dayOfWeek"]?.jsonPrimitive?.int ?: (idx + 1),
                activityType = obj["activityType"]?.jsonPrimitive?.content ?: "rest",
                workoutType = obj["workoutType"]?.jsonPrimitive?.content ?: "rest",
                description = obj["description"]?.jsonPrimitive?.content ?: "",
                targetDistanceKm = obj["targetDistanceKm"]?.jsonPrimitive?.floatOrNull,
                targetDurationMin = obj["targetDurationMin"]?.jsonPrimitive?.intOrNull,
                intensityZone = obj["intensityZone"]?.jsonPrimitive?.contentOrNull,
                targetPaceSecPerKm = obj["targetPaceSecPerKm"]?.jsonPrimitive?.intOrNull
            )
        }
        TrainingPlan(id = 0, weekStartDate = weekStart, weekEndDate = weekEnd,
            aiInsight = "Plan generated for week of ${java.text.SimpleDateFormat("MMM d").format(weekStart)}",
            workouts = workouts)
    }.getOrNull()
}
