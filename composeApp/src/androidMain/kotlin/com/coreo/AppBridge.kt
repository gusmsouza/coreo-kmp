package com.coreo

import com.coreo.data.AppRepository
import com.coreo.model.Goal
import com.coreo.model.UserSettings
import com.coreo.model.WorkoutSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// These top-level functions are called from commonMain App.kt
// They receive Any? and cast to AppRepository safely

actual fun loadData(
    repository: Any?,
    onLoaded: (List<WorkoutSession>, List<Goal>, UserSettings) -> Unit
) {
    val repo = repository as? AppRepository ?: run {
        onLoaded(emptyList(), emptyList(), UserSettings())
        return
    }
    CoroutineScope(Dispatchers.IO).launch {
        val sessions = repo.getSessions()
        val goals    = repo.getGoals()
        val settings = repo.loadSettings()
        kotlinx.coroutines.withContext(Dispatchers.Main) {
            onLoaded(sessions, goals, settings)
        }
    }
}

actual fun saveSession(repository: Any?, session: WorkoutSession) {
    val repo = repository as? AppRepository ?: return
    CoroutineScope(Dispatchers.IO).launch { repo.insertSession(session) }
}

actual fun deleteSession(repository: Any?, session: WorkoutSession) {
    val repo = repository as? AppRepository ?: return
    CoroutineScope(Dispatchers.IO).launch { repo.deleteSession(session) }
}

actual fun saveSettings(repository: Any?, settings: UserSettings) {
    val repo = repository as? AppRepository ?: return
    CoroutineScope(Dispatchers.IO).launch { repo.saveSettings(settings) }
}

actual fun resetAll(repository: Any?) {
    val repo = repository as? AppRepository ?: return
    CoroutineScope(Dispatchers.IO).launch { repo.resetAll() }
}
