package dev.toufikforyou.colormatching.main.presentation.screens.score

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.toufikforyou.colormatching.main.data.local.entity.HighScore
import dev.toufikforyou.colormatching.main.presentation.components.game.GameBackground
import dev.toufikforyou.colormatching.main.presentation.components.game.GameTopBar
import dev.toufikforyou.colormatching.main.viewmodel.score.HighScoresViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HighScoreScreen(
    navController: NavController
) {
    val viewModel: HighScoresViewModel = koinViewModel()
    val highScores by viewModel.highScores.collectAsState(initial = emptyList())

    Scaffold(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        topBar = {
            GameTopBar(title = "High Scores") {
                navController.navigateUp()
            }
        }) { padding ->
        GameBackground()
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Show scores for each difficulty
            DifficultyScores("Easy Mode", highScores.filter { it.difficulty == "Easy" })
            Spacer(modifier = Modifier.height(16.dp))

            DifficultyScores("Medium Mode", highScores.filter { it.difficulty == "Medium" })
            Spacer(modifier = Modifier.height(16.dp))

            DifficultyScores("Hard Mode", highScores.filter { it.difficulty == "Hard" })

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DifficultyScores(difficulty: String, scores: List<HighScore>) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = difficulty, style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ), color = MaterialTheme.colorScheme.primary
            )

            if (scores.isEmpty()) {
                Text(
                    "No scores yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                ) {
                    mutableListOf("Rank", "Level", "Score", "Date").forEach {
                        Text(
                            text = it, style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ), color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Score Items
                scores.sortedByDescending { it.score }.take(5).forEachIndexed { index, score ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(28.dp).clip(CircleShape).background(
                                    when (index) {
                                        0 -> MaterialTheme.colorScheme.primary
                                        1 -> MaterialTheme.colorScheme.secondary
                                        2 -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                            alpha = 0.5f
                                        )
                                    }
                                ), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${index + 1}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            text = "${score.level}", style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "${score.score}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            text = score.date,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
} 