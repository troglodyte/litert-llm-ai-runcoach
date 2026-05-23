package com.litert.coach.domain.usecase

import com.litert.coach.ai.CoachModel
import com.litert.coach.ai.PromptBuilder
import com.litert.coach.domain.repository.ChatRepository
import kotlinx.coroutines.flow.toList
import javax.inject.Inject

private const val SUMMARIZATION_THRESHOLD = 20

class SummarizeChatUseCase @Inject constructor(
    private val chatRepo: ChatRepository,
    private val model: CoachModel,
    private val promptBuilder: PromptBuilder
) {
    suspend operator fun invoke() {
        val unsummarized = chatRepo.getUnsummarized()
        if (unsummarized.size < SUMMARIZATION_THRESHOLD) return
        val prompt = promptBuilder.buildSummarizationPrompt(unsummarized)
        val summary = model.generate(prompt).toList().joinToString("")
        val lastId = unsummarized.last().id
        chatRepo.insertSummary(summary.trim(), lastId)
        chatRepo.markSummarized(lastId)
    }
}
