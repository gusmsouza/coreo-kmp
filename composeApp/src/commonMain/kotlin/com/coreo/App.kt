package com.coreo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.coreo.model.Goal
import com.coreo.model.UserSettings
import com.coreo.model.WorkoutSession
import com.coreo.theme.CoreoTheme
import com.coreo.ui.Screen
import com.coreo.ui.screens.MainScreen
import com.coreo.ui.screens.RestScreen
import com.coreo.ui.screens.SetSetupScreen
import com.coreo.ui.screens.WorkoutScreen

@Composable
fun App() {
    CoreoTheme {
        // Temporary in-memory state -- replaced with Room/DataStore in Phase 8
        var currentScreen      by remember { mutableStateOf<Screen>(Screen.Main) }
        var sessions           by remember { mutableStateOf<List<WorkoutSession>>(emptyList()) }
        var goals              by remember { mutableStateOf<List<Goal>>(emptyList()) }
        var settings           by remember { mutableStateOf(UserSettings()) }

        // Workout coordinator state
        var numberOfSets       by remember { mutableStateOf(1) }
        var durationPerSet     by remember { mutableStateOf(30) }
        var restDuration       by remember { mutableStateOf(60) }
        var currentSet         by remember { mutableStateOf(1) }
        var completedDurations by remember { mutableStateOf<List<Int>>(emptyList()) }

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
                settings    = settings,
                lastSession = sessions.firstOrNull(),
                onStartWorkout = { sets, duration, rest ->
                    numberOfSets       = sets
                    durationPerSet     = duration
                    restDuration       = rest
                    currentSet         = 1
                    completedDurations = emptyList()
                    currentScreen      = Screen.Workout
                },
                onCancel = { currentScreen = Screen.Main }
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
                        // Save session and return home
                        val newSession = if (numberOfSets == 1) {
                            WorkoutSession.single(duration = duration)
                        } else {
                            WorkoutSession.set(
                                durations    = completedDurations,
                                restDuration = restDuration
                            )
                        }
                        sessions      = listOf(newSession) + sessions
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
                onComplete        = {
                    currentSet++
                    currentScreen = Screen.Workout
                },
                onSkip = {
                    currentSet++
                    currentScreen = Screen.Workout
                }
            )

            // Remaining screens stubbed -- built in Phase 8
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