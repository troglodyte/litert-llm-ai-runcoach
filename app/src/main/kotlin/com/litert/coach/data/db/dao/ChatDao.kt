package com.litert.coach.data.db.dao

import androidx.room.*
import com.litert.coach.data.db.entity.AiContextSummaryEntity
import com.litert.coach.data.db.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY created_at ASC")
    fun observeAll(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE is_summarized = 0 ORDER BY created_at ASC")
    suspend fun getUnsummarized(): List<ChatMessageEntity>

    @Query("SELECT * FROM chat_messages ORDER BY created_at ASC")
    suspend fun getAll(): List<ChatMessageEntity>

    @Insert
    suspend fun insert(message: ChatMessageEntity): Long

    @Query("UPDATE chat_messages SET is_summarized = 1 WHERE id <= :upToId")
    suspend fun markSummarized(upToId: Int)

    @Insert
    suspend fun insertSummary(summary: AiContextSummaryEntity)

    @Query("SELECT * FROM ai_context_summaries ORDER BY created_at DESC LIMIT 1")
    suspend fun getLatestSummary(): AiContextSummaryEntity?

    @Query("DELETE FROM chat_messages")
    suspend fun clearAll()

    @Query("DELETE FROM ai_context_summaries")
    suspend fun clearAllSummaries()
}
