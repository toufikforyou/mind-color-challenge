package dev.toufikforyou.colormatching.main.viewmodel.game

import androidx.lifecycle.ViewModel
import dev.toufikforyou.colormatching.main.data.local.dao.GameProgressDao
import dev.toufikforyou.colormatching.main.data.local.dao.HighScoreDao
import dev.toufikforyou.colormatching.main.data.local.entity.HighScore
import dev.toufikforyou.colormatching.main.domain.model.GameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameViewModel(
    val gameProgressDao: GameProgressDao, val highScoreDao: HighScoreDao,
    // private val notificationHelper: NotificationHelper,
    initialGridSize: Int, private val difficulty: String
) : ViewModel() {
    private val _gameState = MutableStateFlow(
        GameState(
            gridSize = initialGridSize, timeLimit = calculateTimeLimit(1), isGameStarted = false
        )
    )
    val gameState: StateFlow<GameState> = _gameState

    fun updateGameState(update: (GameState) -> GameState) {
        _gameState.value = update(_gameState.value)
    }

    fun calculateTimeLimit(level: Int): Int {
        return when (difficulty) {
            "Easy" -> when {
                level < 10 -> 20
                level < 20 -> 18
                level < 30 -> 16
                level < 40 -> 14
                level < 50 -> 12
                level < 60 -> 10
                level < 70 -> 8
                level < 80 -> 6
                else -> 5
            }

            "Medium" -> when {
                level < 10 -> 60
                level < 20 -> 50
                level < 30 -> 45
                level < 40 -> 40
                level < 50 -> 35
                else -> 30
            }

            "Hard" -> when {
                level < 10 -> 180
                level < 20 -> 150
                level < 30 -> 120
                level < 40 -> 100
                level < 50 -> 80
                level < 60 -> 60
                else -> 50
            }

            else -> 30
        }
    }

    suspend fun saveHighScore(score: Int, level: Int, difficulty: String) {
        if (highScoreDao.isHighScore(difficulty, score)) {
            val newScore = HighScore(
                score = score,
                level = level,
                difficulty = difficulty,
                date = getCurrentDateFormatted()
            )
            highScoreDao.insertHighScore(newScore)
            highScoreDao.deleteOldScores(difficulty, newScore.score)

            // Show notification for new high score
            // notificationHelper.showHighScoreNotification(score, difficulty)
        }
    }
} 