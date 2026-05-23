package com.litert.coach.domain.repository

import com.litert.coach.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeAll(): Flow<List<ChatMessage>>
    suspend fun getUnsummarized(): List<ChatMessage>
    suspend fun getAll(): List<ChatMessage>
    suspend fun insert(message: ChatMessage): Long
    suspend fun markSummarized(upToId: Int)
    suspend fun insertSummary(summaryText: String, coversToMessageId: Int)
    suspend fun getLatestSummaryText(): String?
    suspend fun clearAll()
}
