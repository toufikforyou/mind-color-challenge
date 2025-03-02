package dev.toufikforyou.colormatching.main.presentation.components.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.toufikforyou.colormatching.main.data.local.entity.HighScore

@Composable
fun GameOverDialog(
    score: Int,
    matchedPairs: Int,
    totalPairs: Int,
    difficulty: String,
    level: Int,
    highScores: List<HighScore>,
    onTryAgain: () -> Unit,
    onBack: () -> Unit
) {
    // Filter high scores by current difficulty
    val difficultyHighScores =
        highScores.filter { it.difficulty == difficulty }.sortedByDescending { it.score }.take(3)

    // Check if current score is a high score for this difficulty
    val isHighScore =
        difficultyHighScores.any { it.score <= score } || difficultyHighScores.size < 3

    AlertDialog(onDismissRequest = { }, title = { Text("Game Over!") }, text = {
        Column(
            modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Time's up! Your score: $score")
            Text("Matched pairs: $matchedPairs/$totalPairs")
            Text("Level reached: $level")

            // Show high score message if achieved
            if (isHighScore) {
                Text(
                    "New High Score for $difficulty Mode!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Show top 3 high scores for current difficulty
            Text(
                "$difficulty Mode Top Scores:",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary
            )

            if (difficultyHighScores.isEmpty()) {
                Text(
                    "No high scores yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                ) {
                    mutableListOf("SN", "Level", "Score").forEach {
                        Text(
                            text = it, style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ), color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                difficultyHighScores.forEachIndexed { index, highScore ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween
                    ) {
                        Text(
                            text = "${index + 1}.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${highScore.level}", style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${highScore.score}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                        )
                    }
                }
            }
        }
    }, confirmButton = {
        Button(onClick = onTryAgain) {
            Text("Try Again")
        }
    }, dismissButton = {
        Button(onClick = onBack) {
            Text("Back to Menu")
        }
    })
}

@Composable
fun GameExitDialog(
    onDismiss: () -> Unit, onConfirm: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(
            text = "Exit Game", style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }, text = {
        Text(
            text = "Are you sure you want to exit? Your progress will be lost.",
            style = MaterialTheme.typography.bodyLarge
        )
    }, confirmButton = {
        Button(
            onClick = onConfirm, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Exit Game")
        }
    }, dismissButton = {
        Button(
            onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Continue Playing")
        }
    })
}