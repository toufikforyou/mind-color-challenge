package dev.toufikforyou.colormatching.main.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data object Difficulty : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data object Guide : Screen()

    @Serializable
    data object HighScores : Screen()

    sealed class Game : Screen() {
        @Serializable
        data object Easy : Screen()

        @Serializable
        data object Medium : Screen()

        @Serializable
        data object Hard : Screen()
    }
}