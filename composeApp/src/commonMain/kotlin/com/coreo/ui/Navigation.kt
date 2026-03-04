package com.coreo.ui

sealed class Screen(val route: String) {
    object Main       : Screen("main")
    object History    : Screen("history")
    object Stats      : Screen("stats")
    object GoalSetup  : Screen("goal_setup")
    object Settings   : Screen("settings")
    object SetSetup   : Screen("set_setup")
    object Countdown  : Screen("countdown")
    object Workout    : Screen("workout")
    object Rest       : Screen("rest")
    object Onboarding : Screen("onboarding")
}