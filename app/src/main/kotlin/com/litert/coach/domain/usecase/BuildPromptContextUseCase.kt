package com.litert.coach.domain.usecase

import com.litert.coach.ai.PromptBuilder
import com.litert.coach.domain.repository.ChatRepository
import com.litert.coach.domain.repository.PlanRepository
import com.litert.coach.domain.repository.ProfileRepository
import com.litert.coach.domain.repository.WorkoutRepository
import javax.inject.Inject

class BuildPromptContextUseCase @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val workoutRepo: WorkoutRepository,
    private val planRepo: PlanRepository,
    private val chatRepo: ChatRepository,
    private val historicalAverages: GetHistoricalAveragesUseCase,
    private val promptBuilder: PromptBuilder
) {
    suspend operator fun invoke(): String {
        val profile = profileRepo.get() ?: return ""
        val averages = historicalAverages()
        val recent = workoutRepo.getRecent(7)
        val plan = planRepo.getActivePlan()
        val unsummarized = chatRepo.getUnsummarized()
        val summary = chatRepo.getLatestSummaryText()
        return promptBuilder.buildChatPrompt(
            profile = profile,
            historicalAverages = averages,
            recentWorkouts = recent,
            activePlanWorkouts = plan?.workouts ?: emptyList(),
            chatHistory = unsummarized,
            summaryText = summary
        )
    }
}
