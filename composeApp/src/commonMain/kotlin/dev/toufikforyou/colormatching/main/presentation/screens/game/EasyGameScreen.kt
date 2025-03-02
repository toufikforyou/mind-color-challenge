package dev.toufikforyou.colormatching.main.presentation.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.toufikforyou.colormatching.main.data.local.entity.GameProgress
import dev.toufikforyou.colormatching.main.navigation.BackHandler
import dev.toufikforyou.colormatching.main.presentation.components.game.AnimatedGameScore
import dev.toufikforyou.colormatching.main.presentation.components.game.ColorGrid
import dev.toufikforyou.colormatching.main.presentation.components.game.GameBackground
import dev.toufikforyou.colormatching.main.presentation.components.game.GameExitDialog
import dev.toufikforyou.colormatching.main.presentation.components.game.GameOverDialog
import dev.toufikforyou.colormatching.main.presentation.components.game.GameTimeScore
import dev.toufikforyou.colormatching.main.presentation.components.game.GameTopBar
import dev.toufikforyou.colormatching.main.presentation.components.game.ResumeGameDialog
import dev.toufikforyou.colormatching.main.utils.generateColorPairs
import dev.toufikforyou.colormatching.main.viewmodel.game.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EasyGameScreen(
    navController: NavController, /* soundManager: SoundManager, isSoundEnabled: Boolean */
) {
    val viewModel: GameViewModel = koinViewModel { parametersOf(3, "Easy") }
    val gameState by viewModel.gameState.collectAsState()

    // Add this to collect high scores
    val highScores by viewModel.highScoreDao.getHighScoresByDifficulty("Easy")
        .collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()
    var showGameOverDialog by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableIntStateOf(gameState.timeLimit) }
    var showInitialColors by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var isTimerPaused by remember { mutableStateOf(false) }

    var mutableColorBoxes by remember {
        mutableStateOf(generateColorPairs(gameState.gridSize))
    }

    val selectedBoxes = remember { mutableStateListOf<Int>() }

    // Calculate total pairs for current grid size
    val totalPairs = remember(gameState.gridSize) {
        (gameState.gridSize * gameState.gridSize) / 2
    }

    // Add state for showing resume dialog
    var showResumeDialog by remember { mutableStateOf(false) }
    var savedProgress by remember { mutableStateOf<GameProgress?>(null) }

    // Check for saved progress when screen opens
    LaunchedEffect(Unit) {
        savedProgress = viewModel.gameProgressDao.getProgress("Easy").firstOrNull()
        if (savedProgress != null) {
            showResumeDialog = true
        }
    }

    // Save progress when game state changes or when leaving the screen
    DisposableEffect(gameState) {
        onDispose {
            if (gameState.currentLevel > 1) {
                scope.launch {
                    viewModel.gameProgressDao.saveProgress(
                        GameProgress(
                            difficulty = "Easy",
                            level = gameState.currentLevel,
                            score = gameState.score
                        )
                    )
                }
            }
        }
    }

    // Handle box selection function
    val handleBoxSelection = { index: Int ->
        if (!mutableColorBoxes[index].isMatched && !selectedBoxes.contains(index) && selectedBoxes.size < 2) {
            // Update the selected state of the clicked box
            mutableColorBoxes = mutableColorBoxes.mapIndexed { i, box ->
                if (i == index) {
                    box.copy(isSelected = true)
                } else {
                    box
                }
            }

            selectedBoxes.add(index)

            if (selectedBoxes.size == 2) {
                val firstIndex = selectedBoxes[0]
                val secondIndex = selectedBoxes[1]

                if (mutableColorBoxes[firstIndex].color == mutableColorBoxes[secondIndex].color) {
                    // Match found
                    scope.launch {
                        delay(300) // Short delay before showing match
                        // if (isSoundEnabled) soundManager.playMatchFound()
                        mutableColorBoxes = mutableColorBoxes.mapIndexed { i, box ->
                            if (i == firstIndex || i == secondIndex) {
                                box.copy(isMatched = true, isSelected = false)
                            } else {
                                box
                            }
                        }

                        val newMatchedPairs = gameState.matchedPairs + 1
                        viewModel.updateGameState {
                            it.copy(
                                matchedPairs = newMatchedPairs, score = it.score + 10
                            )
                        }

                        // Clear selected boxes after match
                        selectedBoxes.clear()

                        // Level up only if ALL pairs are matched
                        if (newMatchedPairs == totalPairs) {
                            // if (isSoundEnabled) soundManager.playLevelComplete()
                            val nextLevel = gameState.currentLevel + 1
                            // Prepare next level
                            viewModel.updateGameState {
                                it.copy(
                                    currentLevel = nextLevel,
                                    timeLimit = viewModel.calculateTimeLimit(nextLevel),
                                    matchedPairs = 0,
                                    isGameStarted = false
                                )
                            }

                            // Reset game state for new level
                            timeLeft = gameState.timeLimit
                            mutableColorBoxes = generateColorPairs(gameState.gridSize)
                            showInitialColors = true
                        }
                    }
                } else {
                    // No match - hide cards after delay
                    scope.launch {
                        delay(500)
                        mutableColorBoxes = mutableColorBoxes.mapIndexed { i, box ->
                            if (i == firstIndex || i == secondIndex) {
                                box.copy(isSelected = false)
                            } else {
                                box
                            }
                        }
                        selectedBoxes.clear()
                    }
                }
            }
        }
    }

    // Game timer with pause support
    LaunchedEffect(gameState.isGameStarted, gameState.currentLevel, isTimerPaused) {
        if (gameState.isGameStarted && !isTimerPaused) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
                if (timeLeft == 0) {
                    viewModel.updateGameState {
                        it.copy(isGameStarted = false)
                    }
                    showGameOverDialog = true
                }
            }
        }
    }

    // Initial color display timer
    LaunchedEffect(showInitialColors) {
        if (showInitialColors) {
            delay(3000) // 3 seconds for easy mode
            showInitialColors = false
            viewModel.updateGameState {
                it.copy(isGameStarted = true)
            }
        }
    }

    // Handle back press
    BackHandler {
        if (gameState.isGameStarted) {
            showExitDialog = true
            isTimerPaused = true
        } else {
            navController.navigateUp()
        }
    }

    // Update the game over dialog section
    if (showGameOverDialog) {
        LaunchedEffect(Unit) {
            viewModel.saveHighScore(
                score = gameState.score, level = gameState.currentLevel, difficulty = "Easy"
            )
        }

        GameOverDialog(score = gameState.score,
            matchedPairs = gameState.matchedPairs,
            totalPairs = totalPairs,
            difficulty = "Easy",
            level = gameState.currentLevel,
            highScores = highScores,
            onTryAgain = {
                showGameOverDialog = false
                viewModel.updateGameState { currentState ->
                    currentState.copy(
                        timeLimit = viewModel.calculateTimeLimit(currentState.currentLevel),
                        matchedPairs = 0,
                        isGameStarted = false,
                        score = currentState.score
                    )
                }
                timeLeft = viewModel.calculateTimeLimit(gameState.currentLevel)
                mutableColorBoxes = generateColorPairs(gameState.gridSize)
                selectedBoxes.clear()
                showInitialColors = true
            },
            onBack = {
                navController.navigateUp()
            })
    }

    // Add resume dialog
    if (showResumeDialog && savedProgress != null) {
        ResumeGameDialog(difficulty = "Easy",
            level = savedProgress!!.level,
            score = savedProgress!!.score,
            onResume = {
                showResumeDialog = false
                viewModel.updateGameState {
                    it.copy(
                        gridSize = 3,
                        timeLimit = viewModel.calculateTimeLimit(savedProgress!!.level),
                        isGameStarted = false,
                        currentLevel = savedProgress!!.level,
                        score = savedProgress!!.score
                    )
                }
                timeLeft = gameState.timeLimit
                mutableColorBoxes = generateColorPairs(gameState.gridSize)
            },
            onNewGame = {
                showResumeDialog = false
                scope.launch {
                    viewModel.gameProgressDao.deleteProgress("Easy")
                }
            },
            onDismiss = {
                navController.navigateUp()
            })
    }

    // Add this LaunchedEffect to auto-start the game
    LaunchedEffect(showGameOverDialog, showResumeDialog) {
        if (!showGameOverDialog && !showResumeDialog && !showExitDialog) {
            delay(500) // Small delay to ensure dialogs are fully hidden
            showInitialColors = true
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        topBar = {
            GameTopBar(title = "Easy Level ${gameState.currentLevel}") {
                if (gameState.isGameStarted) {
                    showExitDialog = true
                    isTimerPaused = true
                } else {
                    navController.navigateUp()
                }
            }
        }) { paddingValues ->
        GameBackground()
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Game stats
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GameTimeScore(title = "Time", timeLeft = timeLeft)
                AnimatedGameScore("Score", gameState.score.toString())
                AnimatedGameScore("Level", gameState.currentLevel.toString())
            }

            // Color grid
            ColorGrid(gridSize = gameState.gridSize,
                colorBoxes = mutableColorBoxes,
                showInitialColors = showInitialColors,
                onBoxClick = { index ->
                    if (!showInitialColors && gameState.isGameStarted && !mutableColorBoxes[index].isMatched) {
                        handleBoxSelection(index)
                    }
                })
        }

        // Show exit confirmation dialog
        if (showExitDialog) {
            GameExitDialog(onDismiss = {
                showExitDialog = false
                isTimerPaused = false
            }, onConfirm = {
                // if (isSoundEnabled) soundManager.playButtonClick()
                navController.navigateUp()
            })
        }
    }
}