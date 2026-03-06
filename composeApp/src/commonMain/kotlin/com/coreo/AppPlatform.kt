package com.coreo

import com.coreo.model.Goal
import com.coreo.model.UserSettings
import com.coreo.model.WorkoutSession

expect fun loadData(
    repository: Any?,
    onLoaded: (List<WorkoutSession>, List<Goal>, UserSettings) -> Unit
)

expect fun saveSession(repository: Any?, session: WorkoutSession)

expect fun deleteSession(repository: Any?, session: WorkoutSession)

expect fun saveSettings(repository: Any?, settings: UserSettings)

expect fun resetAll(repository: Any?)
