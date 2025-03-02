package dev.toufikforyou.colormatching.main.presentation.screens.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import dev.toufikforyou.colormatching.main.presentation.components.game.GameBackground
import dev.toufikforyou.colormatching.main.presentation.components.game.GameTopBar
import dev.toufikforyou.colormatching.main.presentation.components.guide.BulletPoint
import dev.toufikforyou.colormatching.main.presentation.components.guide.GuideSection

@Composable
fun GuideScreen(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        topBar = {
            GameTopBar(
                title = "How to Play"
            ) {
                navController.navigateUp()
            }
        }) { padding ->
        GameBackground()
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            GuideSection(title = "Game Modes", icon = Icons.Default.Build, content = {
                BulletPoint("Easy: 3x3 grid, perfect for beginners")
                BulletPoint("Medium: 4x4 grid, increased challenge")
                BulletPoint("Hard: 5x5 grid, for expert players")
            })

            GuideSection(title = "How to Play", icon = Icons.Default.PlayArrow, content = {
                BulletPoint("Memorize the colors shown initially")
                BulletPoint("Click pairs of boxes to match colors")
                BulletPoint("Match all pairs before time runs out")
                BulletPoint("Quick matches earn bonus points")
            })

            GuideSection(title = "Scoring System", icon = Icons.Default.Star, content = {
                BulletPoint("Base points for each match: 10")
                BulletPoint("Quick match bonus: +5 to +15")
                BulletPoint("Streak bonus: +5 per match")
                BulletPoint("Time bonus for early completion")
            })

            GuideSection(title = "Tips & Tricks", icon = Icons.Default.Settings, content = {
                BulletPoint("Focus on remembering color patterns")
                BulletPoint("Start with easy mode to practice")
                BulletPoint("Try to maintain matching streaks")
                BulletPoint("Watch the timer for urgency")
            })
        }
    }
}

