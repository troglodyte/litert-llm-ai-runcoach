package com.litert.coach.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_context_summaries")
data class AiContextSummaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "summary_text") val summaryText: String,
    @ColumnInfo(name = "covers_to_message_id") val coversToMessageId: Int,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
