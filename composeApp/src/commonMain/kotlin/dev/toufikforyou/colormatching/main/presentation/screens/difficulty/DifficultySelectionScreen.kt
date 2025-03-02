package dev.toufikforyou.colormatching.main.presentation.screens.difficulty

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.toufikforyou.colormatching.main.navigation.Screen
import dev.toufikforyou.colormatching.main.presentation.components.difficulty.DifficultyButton
import dev.toufikforyou.colormatching.main.presentation.components.game.GameBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultySelectionScreen(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize().background(
            MaterialTheme.colorScheme.background
        ), topBar = {
        TopAppBar(title = {
            Text(
                "Select Difficulty", style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            )
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
        )
    }) { paddingValues ->
        GameBackground()
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DifficultyButton(text = "Easy",
                description = "3x3 Grid",
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = { navController.navigate(Screen.Game.Easy) })

            Spacer(modifier = Modifier.height(24.dp))

            DifficultyButton(text = "Medium",
                description = "4x4 Grid",
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { navController.navigate(Screen.Game.Medium) })

            Spacer(modifier = Modifier.height(24.dp))

            DifficultyButton(text = "Hard",
                description = "5x5 Grid",
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                onClick = { navController.navigate(Screen.Game.Hard) })
        }
    }
}