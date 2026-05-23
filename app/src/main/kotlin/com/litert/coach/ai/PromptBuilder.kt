package com.litert.coach.ai

import com.litert.coach.data.db.dao.HistoricalAverages
import com.litert.coach.domain.model.ChatMessage
import com.litert.coach.domain.model.PlannedWorkout
import com.litert.coach.domain.model.UserProfile
import com.litert.coach.domain.model.WorkoutLog
import javax.inject.Inject
import javax.inject.Singleton

private val DAYS = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

private const val SYSTEM_PROMPT = """
You are a supportive, knowledgeable running and cycling coach.
Your role is to guide athletes of all levels with personalized training advice based on their profile, history, and goals.
Be concise, encouraging, and evidence-based. Refer to the user's actual data when giving advice.
When asked to create or modify a plan, respond only with structured JSON (no markdown fences).
"""

@Singleton
class PromptBuilder @Inject constructor() {

    fun buildChatPrompt(
        profile: UserProfile,
        historicalAverages: HistoricalAverages?,
        recentWorkouts: List<WorkoutLog>,
        activePlanWorkouts: List<PlannedWorkout>,
        chatHistory: List<ChatMessage>,
        summaryText: String?
    ): String = buildString {
        appendLine(SYSTEM_PROMPT.trim())
        appendLine("\n--- USER PROFILE ---")
        appendLine(formatProfile(profile))
        appendLine("\n--- TRAINING BASELINE (last ${profile.historyWindowMonths} months) ---")
        appendLine(formatHistoricalAverages(historicalAverages))
        appendLine("\n--- RECENT WORKOUTS (last 7 sessions) ---")
        appendLine(formatRecentWorkouts(recentWorkouts))
        appendLine("\n--- THIS WEEK'S PLAN ---")
        appendLine(formatWeekPlan(activePlanWorkouts))
        appendLine("\n--- CONVERSATION ---")
        summaryText?.let { appendLine("[Earlier conversation summary: $it]") }
        chatHistory.forEach { msg ->
            val tag = if (msg.role == "user") "User" else "Coach"
            appendLine("$tag: ${msg.content}")
        }
        append("Coach:")
    }

    fun buildPlanGenerationPrompt(profile: UserProfile, historicalAverages: HistoricalAverages?): String =
        buildString {
            appendLine(SYSTEM_PROMPT.trim())
            appendLine("\n--- USER PROFILE ---")
            appendLine(formatProfile(profile))
            appendLine("\n--- TRAINING BASELINE ---")
            appendLine(formatHistoricalAverages(historicalAverages))
            appendLine("""

--- TASK ---
Generate a 7-day training plan for the coming week. Respond ONLY with a JSON array. No markdown.
Each element: {"dayOfWeek":1,"activityType":"run|cycle|rest","workoutType":"easy|interval|long|tempo|rest",
"description":"string","targetDistanceKm":null,"targetDurationMin":null,"intensityZone":null,"targetPaceSecPerKm":null}
dayOfWeek: 1=Mon, 7=Sun. Include rest days. Tailor to the user's level, goal, and availability.
            """.trimIndent())
        }

    fun buildSummarizationPrompt(messages: List<ChatMessage>): String = buildString {
        appendLine("Summarize the following coaching conversation in 3-5 sentences, preserving key advice, goals mentioned, and any decisions made.")
        appendLine()
        messages.forEach { msg ->
            val tag = if (msg.role == "user") "User" else "Coach"
            appendLine("$tag: ${msg.content}")
        }
        appendLine("\nSummary:")
    }

    private fun formatProfile(p: UserProfile) = buildString {
        appendLine("Name: ${p.name}, Age: ${p.age}, Weight: ${p.weightKg}kg, Height: ${p.heightCm}cm")
        appendLine("Activities: ${p.activityTypes.joinToString()}, Level: ${p.fitnessLevel}, Style: ${p.trainingStyle}")
        appendLine("Available: ${p.weeklyAvailabilityDays} days/week, Current load: ${p.currentWeeklyDistanceKm}km/week")
        p.targetEventName?.let { appendLine("Target event: $it, Distance: ${p.targetEventDistanceKm}km, Date: ${p.targetEventDate?.let { d -> java.text.SimpleDateFormat("yyyy-MM-dd").format(d) } ?: "TBD"}") }
        p.maxHeartRate?.let { appendLine("Max HR: ${it}bpm") }
        p.benchmarkPaceSecPerKm?.let { appendLine("Benchmark pace: ${formatPace(it)}") }
        p.injuriesNotes?.let { appendLine("Notes/limitations: $it") }
    }

    private fun formatHistoricalAverages(h: HistoricalAverages?) = if (h == null) "No history yet." else buildString {
        appendLine("Avg weekly distance: ${"%.1f".format(h.avgWeeklyDistanceKm)}km, Avg weekly duration: ${formatDuration(h.avgWeeklyDurationSeconds.toInt())}")
        appendLine("Avg pace: ${if (h.avgPaceSecPerKm > 0) formatPace(h.avgPaceSecPerKm.toInt()) else "N/A"}, Avg HR: ${if (h.avgHeartRate > 0) "${h.avgHeartRate.toInt()}bpm" else "N/A"}")
        appendLine("Avg effort: ${"%.1f".format(h.avgPerceivedEffort)}/10, Sessions/week: ${"%.1f".format(h.sessionsPerWeek)}")
        appendLine("Longest session: ${"%.1f".format(h.longestDistanceKm)}km, Runs: ${h.runCount}, Cycles: ${h.cycleCount}")
    }

    private fun formatRecentWorkouts(logs: List<WorkoutLog>) =
        if (logs.isEmpty()) "No workouts logged yet."
        else logs.joinToString("\n") { log ->
            val date = java.text.SimpleDateFormat("MM-dd").format(log.loggedAt)
            val dist = log.distanceKm?.let { "${"%.1f".format(it)}km" } ?: "-"
            val pace = log.avgPaceSecPerKm?.let { formatPace(it) } ?: "-"
            val hr = log.avgHeartRate?.let { "${it}bpm" } ?: "-"
            "$date | ${log.activityType} | $dist | $pace | HR:$hr | Effort:${log.perceivedEffort}/10"
        }

    private fun formatWeekPlan(workouts: List<PlannedWorkout>) =
        if (workouts.isEmpty()) "No plan generated yet."
        else workouts.joinToString("\n") { w ->
            val day = DAYS.getOrElse(w.dayOfWeek - 1) { "Day ${w.dayOfWeek}" }
            val dist = w.targetDistanceKm?.let { "${"%.1f".format(it)}km" } ?: ""
            "$day: ${w.workoutType} ${w.activityType} $dist — ${w.description}"
        }

    private fun formatPace(secPerKm: Int): String {
        val min = secPerKm / 60
        val sec = secPerKm % 60
        return "%d:%02d/km".format(min, sec)
    }

    private fun formatDuration(seconds: Int): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        return if (h > 0) "${h}h ${m}m" else "${m}m"
    }
}
