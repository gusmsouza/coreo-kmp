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

@Composable
fun App() {
    CoreoTheme {
        // Temporary in-memory state -- will be replaced with
        // real persistence (Room / DataStore) in Phase 8
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }
        var sessions      by remember { mutableStateOf<List<WorkoutSession>>(emptyList()) }
        var goals         by remember { mutableStateOf<List<Goal>>(emptyList()) }
        var settings      by remember { mutableStateOf(UserSettings()) }

        when (currentScreen) {
            is Screen.Main -> MainScreen(
                sessions       = sessions,
                goals          = goals,
                settings       = settings,
                onStartWorkout = { currentScreen = Screen.SetSetup },
                onOpenHistory  = { currentScreen = Screen.History },
                onOpenStats    = { currentScreen = Screen.Stats },
                onOpenGoalSetup = { currentScreen = Screen.GoalSetup },
                onOpenSettings = { currentScreen = Screen.Settings }
            )
            // Remaining screens will be added in subsequent phases
            else -> MainScreen(
                sessions       = sessions,
                goals          = goals,
                settings       = settings,
                onStartWorkout = { currentScreen = Screen.SetSetup },
                onOpenHistory  = { currentScreen = Screen.History },
                onOpenStats    = { currentScreen = Screen.Stats },
                onOpenGoalSetup = { currentScreen = Screen.GoalSetup },
                onOpenSettings = { currentScreen = Screen.Settings }
            )
        }
    }
}