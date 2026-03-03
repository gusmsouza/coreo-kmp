package com.coreo.model

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.until

data class Goal(
    val id: String,
    val targetWorkouts: Int,
    val weeks: Int,
    val startDate: Instant = Clock.System.now(),
    val completedWorkouts: Int = 0,
    val isActive: Boolean = true
) {
    private val tz get() = TimeZone.currentSystemDefault()

    val endDate: Instant get() =
        startDate.plus(weeks * 7, DateTimeUnit.DAY, tz)

    val daysRemaining: Int get() {
        val now = Clock.System.now()
        return if (now >= endDate) 0
        else now.until(endDate, DateTimeUnit.DAY, tz).toInt()
    }

    val isExpired: Boolean get() = Clock.System.now() > endDate
    val isCompleted: Boolean get() = completedWorkouts >= targetWorkouts

    val progressPercentage: Double get() {
        if (targetWorkouts <= 0) return 0.0
        return minOf(completedWorkouts.toDouble() / targetWorkouts.toDouble() * 100.0, 100.0)
    }

    val status: GoalStatus get() {
        if (isCompleted) return GoalStatus.COMPLETED
        if (isExpired)   return GoalStatus.EXPIRED

        val now       = Clock.System.now()
        val totalMs   = (endDate - startDate).inWholeMilliseconds.toDouble()
        val elapsedMs = (now - startDate).inWholeMilliseconds.toDouble()

        if (totalMs <= 0) return GoalStatus.ON_TRACK

        val expectedProgress = elapsedMs / totalMs
        val actualProgress   = completedWorkouts.toDouble() / targetWorkouts.toDouble()

        return when {
            actualProgress >= expectedProgress        -> GoalStatus.ON_TRACK
            actualProgress >= expectedProgress * 0.8  -> GoalStatus.SLIGHTLY_BEHIND
            else                                      -> GoalStatus.BEHIND
        }
    }
}

enum class GoalStatus {
    ON_TRACK,
    SLIGHTLY_BEHIND,
    BEHIND,
    COMPLETED,
    EXPIRED;

    val displayText: String get() = when (this) {
        ON_TRACK        -> "No ritmo"
        SLIGHTLY_BEHIND -> "Quase lá"
        BEHIND          -> "Acelera!"
        COMPLETED       -> "Concluído!"
        EXPIRED         -> "Expirado"
    }

    val colors: Pair<String, String> get() = when (this) {
        ON_TRACK        -> "#A8C26E" to "#1E4D3B"
        SLIGHTLY_BEHIND -> "#E8913A" to "#F5F0E8"
        BEHIND          -> "#E8913A" to "#3D3D3D"
        COMPLETED       -> "#A8C26E" to "#1E4D3B"
        EXPIRED         -> "#3D3D3D" to "#F5F0E8"
    }
}