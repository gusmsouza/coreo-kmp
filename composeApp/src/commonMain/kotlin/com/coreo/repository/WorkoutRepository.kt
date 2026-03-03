package com.coreo.repository

import com.coreo.model.DayType
import com.coreo.model.Goal
import com.coreo.model.UserSettings
import com.coreo.model.WorkoutSession
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

class WorkoutRepository {

    // MARK: - Streak Calculation
    //
    // Rules (mirrors MainView.swift exactly):
    //  - Training day (plank/intensity) with session  -> +1
    //  - Training day without session                 -> chain broken -> 0
    //  - Rest day without session                     -> transparent (skip, no break, no add)
    //  - Rest day with session                        -> +1 (bonus, welcomed)

    fun calculateStreak(
        sessions: List<WorkoutSession>,
        settings: UserSettings
    ): Int {
        if (sessions.isEmpty()) return 0

        val tz      = TimeZone.currentSystemDefault()
        val today   = Clock.System.todayIn(tz)

        val workoutDays = sessions
            .map { it.date.toLocalDateTime(tz).date }
            .toSet()

        val mostRecent = workoutDays.maxOrNull() ?: return 0

        // Phase 1: check gap from today back to mostRecent is all rest days
        var checkDay = today
        while (checkDay > mostRecent) {
            if (!workoutDays.contains(checkDay) && settings.dayType(checkDay.dayOfWeekNumber) != DayType.REST) {
                return 0
            }
            checkDay = checkDay.minus(1, DateTimeUnit.DAY)
        }

        // Phase 2: count backwards
        var streak = 0
        var day    = mostRecent
        val limit  = today.minus(730, DateTimeUnit.DAY)

        while (day >= limit) {
            when {
                workoutDays.contains(day) -> {
                    streak++
                    day = day.minus(1, DateTimeUnit.DAY)
                }
                settings.dayType(day.dayOfWeekNumber) == DayType.REST -> {
                    day = day.minus(1, DateTimeUnit.DAY) // transparent
                }
                else -> break // missed a training day
            }
        }

        return streak
    }

    // MARK: - Statistics

    fun personalRecord(sessions: List<WorkoutSession>): Int =
        sessions.maxOfOrNull { it.longestRep } ?: 0

    fun totalTimeAllTime(sessions: List<WorkoutSession>): Int =
        sessions.sumOf { it.totalExerciseTime }

    fun last7DaysTotal(sessions: List<WorkoutSession>): Int {
        val tz  = TimeZone.currentSystemDefault()
        val cut = Clock.System.now().minus(7, DateTimeUnit.DAY, tz)
        return sessions
            .filter { it.date >= cut }
            .sumOf { it.totalExerciseTime }
    }

    fun previous7DaysTotal(sessions: List<WorkoutSession>): Int {
        val tz  = TimeZone.currentSystemDefault()
        val d14 = Clock.System.now().minus(14, DateTimeUnit.DAY, tz)
        val d7  = Clock.System.now().minus(7, DateTimeUnit.DAY, tz)
        return sessions
            .filter { it.date >= d14 && it.date < d7 }
            .sumOf { it.totalExerciseTime }
    }

    fun evolutionPercentage(sessions: List<WorkoutSession>): Int? {
        val prev = previous7DaysTotal(sessions)
        if (prev == 0) return null
        val last = last7DaysTotal(sessions)
        return ((last - prev).toDouble() / prev.toDouble() * 100).toInt()
    }

    fun workoutsThisWeek(sessions: List<WorkoutSession>): Int {
        val tz    = TimeZone.currentSystemDefault()
        val today = Clock.System.todayIn(tz)
        // Start of ISO week (Monday)
        val startOfWeek = today.minus(today.dayOfWeek.ordinal, DateTimeUnit.DAY)
        return sessions.count {
            it.date.toLocalDateTime(tz).date >= startOfWeek
        }
    }

    // MARK: - Goal Logic

    fun updateGoalProgress(
        goal: Goal,
        sessions: List<WorkoutSession>
    ): Goal {
        // Count sessions since goal start date
        val completed = sessions.count { it.date >= goal.startDate }
        return goal.copy(completedWorkouts = completed)
    }

    fun activeGoal(goals: List<Goal>): Goal? =
        goals.firstOrNull { it.isActive && !it.isExpired }

    // MARK: - Stats for StatsView

    fun last7DaysChartData(sessions: List<WorkoutSession>): List<DayData> {
        val tz    = TimeZone.currentSystemDefault()
        val today = Clock.System.todayIn(tz)
        return (6 downTo 0).map { daysAgo ->
            val date  = today.minus(daysAgo, DateTimeUnit.DAY)
            val total = sessions
                .filter { it.date.toLocalDateTime(tz).date == date }
                .sumOf { it.totalExerciseTime }
            DayData(date = date, totalSeconds = total)
        }
    }

    fun last12WeeksCalendarData(
        sessions: List<WorkoutSession>,
        settings: UserSettings
    ): List<CalendarDay> {
        val tz    = TimeZone.currentSystemDefault()
        val today = Clock.System.todayIn(tz)
        return (83 downTo 0).map { daysAgo ->
            val date    = today.minus(daysAgo, DateTimeUnit.DAY)
            val count   = sessions.count { it.date.toLocalDateTime(tz).date == date }
            val dayType = settings.dayType(date.dayOfWeekNumber)
            CalendarDay(date = date, count = count, dayType = dayType)
        }
    }

    fun averageTime(sessions: List<WorkoutSession>): Int {
        if (sessions.isEmpty()) return 0
        return sessions.sumOf { it.totalExerciseTime } / sessions.size
    }

    fun bestDayOfWeek(sessions: List<WorkoutSession>): Pair<String, Int> {
        if (sessions.isEmpty()) return Pair("--", 0)
        val tz       = TimeZone.currentSystemDefault()
        val grouped  = sessions.groupBy { it.date.toLocalDateTime(tz).date.dayOfWeek }
        val best     = grouped.maxByOrNull { it.value.size } ?: return Pair("--", 0)
        return Pair(best.key.name.lowercase().replaceFirstChar { it.uppercase() }, best.value.size)
    }

    fun biggestSet(sessions: List<WorkoutSession>): WorkoutSession? =
        sessions.filter { it.isSet }.maxByOrNull { it.totalExerciseTime }
}

// MARK: - Data Classes for Charts

data class DayData(
    val date: LocalDate,
    val totalSeconds: Int
)

data class CalendarDay(
    val date: LocalDate,
    val count: Int,
    val dayType: DayType
)

// MARK: - Extension: ISO weekday number
// kotlinx-datetime DayOfWeek: MONDAY=1 ... SUNDAY=7
// Matches Calendar weekday used in UserSettings (where 1=Sun, 2=Mon ... 7=Sat on iOS)
// We normalize here so UserSettings.dayType() works correctly on both platforms

val LocalDate.dayOfWeekNumber: Int get() = when (dayOfWeek) {
    DayOfWeek.SUNDAY    -> 1
    DayOfWeek.MONDAY    -> 2
    DayOfWeek.TUESDAY   -> 3
    DayOfWeek.WEDNESDAY -> 4
    DayOfWeek.THURSDAY  -> 5
    DayOfWeek.FRIDAY    -> 6
    DayOfWeek.SATURDAY  -> 7
    else                -> 1
}