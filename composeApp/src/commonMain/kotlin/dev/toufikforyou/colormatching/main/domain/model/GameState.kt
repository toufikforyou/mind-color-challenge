package dev.toufikforyou.colormatching.main.domain.model

data class GameState(
    val gridSize: Int,
    val timeLimit: Int,
    val currentLevel: Int = 1,
    val score: Int = 0,
    val matchedPairs: Int = 0,
    val isGameStarted: Boolean = false
) 