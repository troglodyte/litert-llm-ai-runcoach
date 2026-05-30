package com.litert.coach.ai

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class PromptEntry(
    val id: Long,
    val prompt: String,
    val timestamp: Long,
    val label: String
) {
    val formattedTime: String
        get() = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
}

@Singleton
class PromptHistory @Inject constructor() {
    private val entries = mutableListOf<PromptEntry>()
    private var nextId = 0L

    fun record(prompt: String, label: String = "") {
        entries.add(PromptEntry(nextId++, prompt, System.currentTimeMillis(), label))
    }

    fun getAll(): List<PromptEntry> = entries.toList().reversed()

    fun clear() {
        entries.clear()
        nextId = 0L
    }
}
