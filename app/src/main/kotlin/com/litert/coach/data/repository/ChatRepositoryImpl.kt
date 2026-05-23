package com.litert.coach.data.repository

import com.litert.coach.data.db.dao.ChatDao
import com.litert.coach.data.db.entity.AiContextSummaryEntity
import com.litert.coach.domain.model.ChatMessage
import com.litert.coach.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(private val dao: ChatDao) : ChatRepository {
    override fun observeAll(): Flow<List<ChatMessage>> = dao.observeAll().map { list -> list.map { it.toDomain() } }
    override suspend fun getUnsummarized() = dao.getUnsummarized().map { it.toDomain() }
    override suspend fun getAll() = dao.getAll().map { it.toDomain() }
    override suspend fun insert(message: ChatMessage): Long = dao.insert(message.toEntity())
    override suspend fun markSummarized(upToId: Int) = dao.markSummarized(upToId)
    override suspend fun insertSummary(summaryText: String, coversToMessageId: Int) =
        dao.insertSummary(AiContextSummaryEntity(summaryText = summaryText, coversToMessageId = coversToMessageId))
    override suspend fun getLatestSummaryText() = dao.getLatestSummary()?.summaryText
    override suspend fun clearAll() { dao.clearAll(); dao.clearAllSummaries() }
}
