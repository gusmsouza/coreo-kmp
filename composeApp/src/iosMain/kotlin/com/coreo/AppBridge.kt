package com.coreo

import com.coreo.model.Goal
import com.coreo.model.UserSettings
import com.coreo.model.WorkoutSession

// iOS persistence implemented in Phase 11
actual fun loadData(repository: Any?, onLoaded: (List<WorkoutSession>, List<Goal>, UserSettings) -> Unit) {
    onLoaded(emptyList(), emptyList(), UserSettings())
}
actual fun saveSession(repository: Any?, session: WorkoutSession) {}
actual fun deleteSession(repository: Any?, session: WorkoutSession) {}
actual fun saveSettings(repository: Any?, settings: UserSettings) {}
actual fun resetAll(repository: Any?) {}
