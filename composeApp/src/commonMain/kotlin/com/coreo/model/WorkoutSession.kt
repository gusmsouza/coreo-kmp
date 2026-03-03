package com.coreo.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class WorkoutSession(
    val id: String,
    val date: Instant = Clock.System.now(),
    // Single workout (backward compatible)
    val duration: Int = 0,
    // Set workout
    val isSet: Boolean = false,
    val durations: List<Int> = emptyList(),
    val totalReps: Int = 1,
    val restDuration: Int = 60
) {
    val totalExerciseTime: Int get() =
        if (isSet) durations.sum() else duration

    val longestRep: Int get() =
        if (isSet) (durations.maxOrNull() ?: 0) else duration

    val completedReps: Int get() =
        if (isSet) durations.size else 1

    val displayText: String get() {
        if (!isSet) return "${duration}s"
        val total = totalExerciseTime
        val minutes = total / 60
        val seconds = total % 60
        val timeString = if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"
        return "${completedReps}x séries ($timeString)"
    }

    companion object {
        // Single workout factory
        fun single(duration: Int, id: String = generateId()): WorkoutSession =
            WorkoutSession(id = id, duration = duration, isSet = false)

        // Set workout factory
        fun set(durations: List<Int>, restDuration: Int, id: String = generateId()): WorkoutSession =
            WorkoutSession(
                id = id,
                duration = durations.maxOrNull() ?: 0,
                isSet = true,
                durations = durations,
                totalReps = durations.size,
                restDuration = restDuration
            )

        private fun generateId(): String =
            Clock.System.now().toEpochMilliseconds().toString()
    }
}