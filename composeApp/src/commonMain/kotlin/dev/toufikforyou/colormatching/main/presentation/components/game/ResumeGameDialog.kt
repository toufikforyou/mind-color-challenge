package dev.toufikforyou.colormatching.main.presentation.components.game

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ResumeGameDialog(
    difficulty: String,
    level: Int,
    score: Int,
    onResume: () -> Unit,
    onNewGame: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = "Previous Game Found",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("You have a saved game in $difficulty mode:\nLevel: $level\nScore: $score\n\nWould you like to resume or start a new game?")
        },
        confirmButton = {
            Button(onClick = onResume) {
                Text("Resume Game")
            }
        },
        dismissButton = {
            Button(onClick = onNewGame) {
                Text("New Game")
            }
        }
    )
} 