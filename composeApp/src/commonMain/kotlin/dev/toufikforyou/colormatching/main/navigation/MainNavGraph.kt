package dev.toufikforyou.colormatching.main.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.toufikforyou.colormatching.main.presentation.screens.difficulty.DifficultySelectionScreen
import dev.toufikforyou.colormatching.main.presentation.screens.game.EasyGameScreen
import dev.toufikforyou.colormatching.main.presentation.screens.game.HardGameScreen
import dev.toufikforyou.colormatching.main.presentation.screens.game.MediumGameScreen
import dev.toufikforyou.colormatching.main.presentation.screens.guide.GuideScreen
import dev.toufikforyou.colormatching.main.presentation.screens.home.HomeScreen
import dev.toufikforyou.colormatching.main.presentation.screens.score.HighScoreScreen

@Composable
fun MainNavGraph() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController, startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(navController = navController)
        }

        composable<Screen.Settings> {
            // SettingsScreen(navController = navController)
        }

        composable<Screen.Difficulty> {
            DifficultySelectionScreen(navController)
        }

        composable<Screen.Guide> {
            GuideScreen(navController)
        }

        composable<Screen.HighScores> {
            HighScoreScreen(navController = navController)
        }

        composable<Screen.Game.Easy> {
            EasyGameScreen(
                navController = navController,
                // soundManager = soundManager,
                // isSoundEnabled = isSoundEnabled
            )
        }

        composable<Screen.Game.Medium> {
            MediumGameScreen(
                navController = navController,
//                soundManager = soundManager,
//                isSoundEnabled = isSoundEnabled
            )
        }

        composable<Screen.Game.Hard> {
            HardGameScreen(
                navController = navController,
//                soundManager = soundManager,
//                isSoundEnabled = isSoundEnabled
            )
        }
    }
}