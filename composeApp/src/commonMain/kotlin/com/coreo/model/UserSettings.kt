package com.coreo.model

data class UserSettings(
    val defaultDuration: Int = 30,
    val defaultRestDuration: Int = 60,
    val alwaysShowCountdown: Boolean = true,
    val notificationsEnabled: Boolean = false,
    val notificationHour: Int = 8,
    val notificationMinute: Int = 0,
    val consecutiveDaysAtDuration: Int = 0,
    val weekSchedule: Map<Int, DayType> = defaultSchedule()
) {
    companion object {
        fun defaultSchedule(): Map<Int, DayType> =
            (1..7).associateWith { DayType.PLANK }

        val durationRange = 10..300
        val restRange     = 15..180
    }

    fun dayType(weekday: Int): DayType =
        weekSchedule[weekday] ?: DayType.PLANK

    val hasIntensityDay: Boolean get() =
        weekSchedule.values.any { it == DayType.INTENSITY }

    val intensityDayCount: Int get() =
        weekSchedule.values.count { it == DayType.INTENSITY }

    fun shouldNudgeProgression(isIntensityDay: Boolean): Boolean =
        if (hasIntensityDay) isIntensityDay && consecutiveDaysAtDuration >= 3
        else consecutiveDaysAtDuration >= 5
}