package com.coreo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.coreo.model.Goal
import com.coreo.model.UserSettings
import com.coreo.model.WorkoutSession
import com.coreo.theme.CoreoTheme
import com.coreo.ui.Screen
import com.coreo.ui.screens.CountdownScreen
import com.coreo.ui.screens.HistoryScreen
import com.coreo.ui.screens.MainScreen
import com.coreo.ui.screens.RestScreen
import com.coreo.ui.screens.SetSetupScreen
import com.coreo.ui.screens.SettingsScreen
import com.coreo.ui.screens.StatsScreen
import com.coreo.ui.screens.WorkoutScreen

// Repository is passed as Any? to keep App.kt in commonMain
// androidMain casts it to AppRepository -- iOS will provide its own in Phase 11
@Composable
fun App(repository: Any? = null) {
    CoreoTheme {
        var currentScreen      by remember { mutableStateOf<Screen>(Screen.Main) }
        var sessions           by remember { mutableStateOf<List<WorkoutSession>>(emptyList()) }
        var goals              by remember { mutableStateOf<List<Goal>>(emptyList()) }
        var settings           by remember { mutableStateOf(UserSettings()) }
        var isLoaded           by remember { mutableStateOf(false) }

        // Workout coordinator state
        var numberOfSets       by remember { mutableIntStateOf(1) }
        var durationPerSet     by remember { mutableIntStateOf(30) }
        var restDuration       by remember { mutableIntStateOf(60) }
        var currentSet         by remember { mutableIntStateOf(1) }
        var completedDurations by remember { mutableStateOf<List<Int>>(emptyList()) }

        // Load persisted data on first launch
        LaunchedEffect(Unit) {
            loadData(repository) { s, g, us ->
                sessions = s
                goals    = g
                settings = us
                isLoaded = true
            }
        }

        when (currentScreen) {

            is Screen.Main -> MainScreen(
                sessions        = sessions,
                goals           = goals,
                settings        = settings,
                onStartWorkout  = { currentScreen = Screen.SetSetup },
                onOpenHistory   = { currentScreen = Screen.History },
                onOpenStats     = { currentScreen = Screen.Stats },
                onOpenGoalSetup = { currentScreen = Screen.GoalSetup },
                onOpenSettings  = { currentScreen = Screen.Settings }
            )

            is Screen.SetSetup -> SetSetupScreen(
                settings       = settings,
                lastSession    = sessions.firstOrNull(),
                onStartWorkout = { sets, duration, rest ->
                    numberOfSets       = sets
                    durationPerSet     = duration
                    restDuration       = rest
                    currentSet         = 1
                    completedDurations = emptyList()
                    currentScreen      = Screen.Countdown
                },
                onCancel = { currentScreen = Screen.Main }
            )

            is Screen.Countdown -> CountdownScreen(
                currentSet     = currentSet,
                numberOfSets   = numberOfSets,
                durationPerSet = durationPerSet,
                onComplete     = { currentScreen = Screen.Workout }
            )

            is Screen.Workout -> WorkoutScreen(
                currentSet     = currentSet,
                numberOfSets   = numberOfSets,
                durationPerSet = durationPerSet,
                onComplete     = { duration ->
                    completedDurations = completedDurations + duration
                    if (currentSet < numberOfSets) {
                        currentScreen = Screen.Rest
                    } else {
                        val newSession = if (numberOfSets == 1) {
                            WorkoutSession.single(duration = duration)
                        } else {
                            WorkoutSession.set(
                                durations    = completedDurations,
                                restDuration = restDuration
                            )
                        }
                        sessions = listOf(newSession) + sessions
                        saveSession(repository, newSession)
                        currentScreen = Screen.Main
                    }
                },
                onCancel = { currentScreen = Screen.Main }
            )

            is Screen.Rest -> RestScreen(
                currentSet        = currentSet,
                numberOfSets      = numberOfSets,
                restDuration      = restDuration,
                completedDuration = completedDurations.lastOrNull() ?: 0,
                onComplete = {
                    currentSet++
                    currentScreen = Screen.Countdown
                },
                onSkip = {
                    currentSet++
                    currentScreen = Screen.Countdown
                }
            )

            is Screen.History -> HistoryScreen(
                sessions        = sessions,
                onDeleteSession = { session ->
                    sessions = sessions.filter { it.id != session.id }
                    deleteSession(repository, session)
                },
                onBack = { currentScreen = Screen.Main }
            )

            is Screen.Stats -> StatsScreen(
                sessions = sessions,
                settings = settings,
                onBack   = { currentScreen = Screen.Main }
            )

            is Screen.Settings -> SettingsScreen(
                settings          = settings,
                onSettingsChanged = { updated ->
                    settings = updated
                    saveSettings(repository, updated)
                },
                onResetAllData = {
                    sessions      = emptyList()
                    goals         = emptyList()
                    settings      = UserSettings()
                    resetAll(repository)
                    currentScreen = Screen.Main
                },
                onBack = { currentScreen = Screen.Main }
            )

            else -> MainScreen(
                sessions        = sessions,
                goals           = goals,
                settings        = settings,
                onStartWorkout  = { currentScreen = Screen.SetSetup },
                onOpenHistory   = { currentScreen = Screen.History },
                onOpenStats     = { currentScreen = Screen.Stats },
                onOpenGoalSetup = { currentScreen = Screen.GoalSetup },
                onOpenSettings  = { currentScreen = Screen.Settings }
            )
        }
    }
}
