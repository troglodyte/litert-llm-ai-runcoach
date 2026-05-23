package com.litert.coach.ai

import kotlinx.coroutines.flow.Flow

enum class ModelVariant(val displayName: String, val fileSizeMb: Int) {
    GEMMA_3N_1B("Gemma 3n 1B (Faster)", 850),
    GEMMA_3N_4B("Gemma 3n 4B (Smarter)", 4400),
    GEMMA3_1B_IT("Gemma 3 1B IT (Community)", 557)
}

interface CoachModel {
    fun setVariant(variant: ModelVariant)
    suspend fun generate(prompt: String): Flow<String>
    suspend fun isReady(): Boolean
    suspend fun load()
    fun unload()
}
