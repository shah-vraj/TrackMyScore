package com.vraj.trackmyscore.util

sealed class MainScreen(val route: String) {
    data object PlayersScreen : MainScreen("PlayersScreen")
    data object LeaderboardScreen : MainScreen("LeaderboardScreen")
    data object GameScreen : MainScreen("GameScreen")
    data object StatsScreen : MainScreen("StatsScreen")
}