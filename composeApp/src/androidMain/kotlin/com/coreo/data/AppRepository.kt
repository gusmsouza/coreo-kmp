package com.coreo.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.coreo.model.DayType
import com.coreo.model.Goal
import com.coreo.model.UserSettings
import com.coreo.model.WorkoutSession
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "coreo_settings")

class AppRepository(private val context: Context) {

    private val db             = buildDatabase(context)
    private val sessionDao     = db.workoutSessionDao()
    private val goalDao        = db.goalDao()

    // MARK: - Keys

    private val KEY_DEFAULT_DURATION     = intPreferencesKey("defaultDuration")
    private val KEY_DEFAULT_REST         = intPreferencesKey("defaultRestDuration")
    private val KEY_SHOW_COUNTDOWN       = booleanPreferencesKey("alwaysShowCountdown")
    private val KEY_NOTIFICATIONS        = booleanPreferencesKey("notificationsEnabled")
    private val KEY_WEEK_SCHEDULE        = stringPreferencesKey("weekSchedule")
    private val KEY_CONSECUTIVE_DAYS     = intPreferencesKey("consecutiveDaysAtDuration")

    // MARK: - Sessions

    suspend fun getSessions(): List<WorkoutSession> =
        sessionDao.getAll().map { it.toModel() }

    suspend fun insertSession(session: WorkoutSession) =
        sessionDao.insert(session.toEntity())

    suspend fun deleteSession(session: WorkoutSession) =
        sessionDao.delete(session.toEntity())

    suspend fun deleteAllSessions() =
        sessionDao.deleteAll()

    // MARK: - Goals

    suspend fun getGoals(): List<Goal> =
        goalDao.getAll().map { it.toModel() }

    suspend fun insertGoal(goal: Goal) =
        goalDao.insert(goal.toEntity())

    suspend fun updateGoal(goal: Goal) =
        goalDao.update(goal.toEntity())

    suspend fun deleteGoal(goal: Goal) =
        goalDao.delete(goal.toEntity())

    suspend fun deleteAllGoals() =
        goalDao.deleteAll()

    // MARK: - Settings

    suspend fun loadSettings(): UserSettings {
        val prefs = context.dataStore.data.first()

        val scheduleStr = prefs[KEY_WEEK_SCHEDULE] ?: ""
        val weekSchedule = if (scheduleStr.isEmpty()) {
            UserSettings.defaultSchedule()
        } else {
            parseSchedule(scheduleStr)
        }

        return UserSettings(
            defaultDuration            = prefs[KEY_DEFAULT_DURATION] ?: 30,
            defaultRestDuration        = prefs[KEY_DEFAULT_REST] ?: 60,
            alwaysShowCountdown        = prefs[KEY_SHOW_COUNTDOWN] ?: true,
            notificationsEnabled       = prefs[KEY_NOTIFICATIONS] ?: false,
            weekSchedule               = weekSchedule,
            consecutiveDaysAtDuration  = prefs[KEY_CONSECUTIVE_DAYS] ?: 0
        )
    }

    suspend fun saveSettings(settings: UserSettings) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_DURATION]  = settings.defaultDuration
            prefs[KEY_DEFAULT_REST]      = settings.defaultRestDuration
            prefs[KEY_SHOW_COUNTDOWN]    = settings.alwaysShowCountdown
            prefs[KEY_NOTIFICATIONS]     = settings.notificationsEnabled
            prefs[KEY_CONSECUTIVE_DAYS]  = settings.consecutiveDaysAtDuration
            prefs[KEY_WEEK_SCHEDULE]     = encodeSchedule(settings.weekSchedule)
        }
    }

    suspend fun resetAll() {
        deleteAllSessions()
        deleteAllGoals()
        saveSettings(UserSettings())
    }

    // MARK: - Schedule Encoding

    private fun encodeSchedule(schedule: Map<Int, DayType>): String =
        schedule.entries.joinToString(";") { (day, type) -> "$day:${type.name}" }

    private fun parseSchedule(str: String): Map<Int, DayType> {
        val result = mutableMapOf<Int, DayType>()
        str.split(";").forEach { entry ->
            val parts = entry.split(":")
            if (parts.size == 2) {
                val day  = parts[0].toIntOrNull() ?: return@forEach
                val type = DayType.entries.find { it.name == parts[1] } ?: DayType.PLANK
                result[day] = type
            }
        }
        // Fill missing days with PLANK
        for (day in 1..7) { result.putIfAbsent(day, DayType.PLANK) }
        return result
    }
}
