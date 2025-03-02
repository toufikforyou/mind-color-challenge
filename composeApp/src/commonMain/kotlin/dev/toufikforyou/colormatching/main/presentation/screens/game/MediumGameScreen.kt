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
import androidx.compose.runtime.mutableLongStateOf
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
import kotlinx.datetime.Clock
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MediumGameScreen(
    navController: NavController, /* soundManager: SoundManager, isSoundEnabled: Boolean */
) {
    val viewModel: GameViewModel = koinViewModel { parametersOf(4, "Medium") }
    val gameState by viewModel.gameState.collectAsState()

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

    // Calculate total pairs for 4x4 grid
    val totalPairs = remember(gameState.gridSize) {
        (gameState.gridSize * gameState.gridSize) / 2
    }

    // Bonus points for quick matches
    var lastMatchTime by remember { mutableLongStateOf(0L) }

    // Collect high scores
    val highScores by viewModel.highScoreDao.getHighScoresByDifficulty("Medium")
        .collectAsState(initial = emptyList())

    // Add state for showing resume dialog
    var showResumeDialog by remember { mutableStateOf(false) }
    var savedProgress by remember { mutableStateOf<GameProgress?>(null) }

    // Check for saved progress when screen opens
    LaunchedEffect(Unit) {
        savedProgress = viewModel.gameProgressDao.getProgress("Medium").firstOrNull()
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
                            difficulty = "Medium",
                            level = gameState.currentLevel,
                            score = gameState.score
                        )
                    )
                }
            }
        }
    }

    // Handle box selection function with combo bonus
    val handleBoxSelection = { index: Int ->
        if (!mutableColorBoxes[index].isMatched && !selectedBoxes.contains(index) && selectedBoxes.size < 2) {
            mutableColorBoxes = mutableColorBoxes.mapIndexed { i, box ->
                if (i == index) box.copy(isSelected = true) else box
            }

            selectedBoxes.add(index)

            if (selectedBoxes.size == 2) {
                val firstIndex = selectedBoxes[0]
                val secondIndex = selectedBoxes[1]

                if (mutableColorBoxes[firstIndex].color == mutableColorBoxes[secondIndex].color) {
                    scope.launch {
                        delay(300)
                        // if (isSoundEnabled) soundManager.playMatchFound()
                        mutableColorBoxes = mutableColorBoxes.mapIndexed { i, box ->
                            if (i == firstIndex || i == secondIndex) {
                                box.copy(isMatched = true, isSelected = false)
                            } else box
                        }

                        val currentTime = Clock.System.now().toEpochMilliseconds()
                        val timeDiff = currentTime - lastMatchTime

                        // Calculate bonus points based on quick matches
                        val bonusPoints = when {
                            timeDiff < 2000 -> 15 // Quick match bonus
                            timeDiff < 3000 -> 10 // Medium speed bonus
                            else -> 5            // Base points
                        }

                        lastMatchTime = currentTime
                        val newMatchedPairs = gameState.matchedPairs + 1
                        viewModel.updateGameState {
                            it.copy(
                                matchedPairs = newMatchedPairs, score = it.score + bonusPoints
                            )
                        }

                        selectedBoxes.clear()

                        if (newMatchedPairs == totalPairs) {
                            // if (isSoundEnabled) soundManager.playLevelComplete()
                            val nextLevel = gameState.currentLevel + 1
                            viewModel.updateGameState {
                                it.copy(
                                    currentLevel = nextLevel,
                                    timeLimit = viewModel.calculateTimeLimit(nextLevel),
                                    matchedPairs = 0,
                                    isGameStarted = false
                                )
                            }

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
                            } else box
                        }
                        selectedBoxes.clear()
                    }
                }
            }
        }
    }

    // Game timer
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
            delay(5000) // 5 seconds for medium mode
            showInitialColors = false
            viewModel.updateGameState {
                it.copy(isGameStarted = true)
            }
        }
    }

    // Add BackHandler
    BackHandler {
        if (gameState.isGameStarted) {
            showExitDialog = true
            isTimerPaused = true
        } else {
            navController.navigateUp()
        }
    }

    if (showGameOverDialog) {
        LaunchedEffect(Unit) {
            viewModel.saveHighScore(
                score = gameState.score, level = gameState.currentLevel, difficulty = "Medium"
            )
        }

        GameOverDialog(score = gameState.score,
            matchedPairs = gameState.matchedPairs,
            totalPairs = totalPairs,
            difficulty = "Medium",
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

    if (showResumeDialog && savedProgress != null) {
        ResumeGameDialog(difficulty = "Medium",
            level = savedProgress!!.level,
            score = savedProgress!!.score,
            onResume = {
                showResumeDialog = false
                viewModel.updateGameState {
                    it.copy(
                        gridSize = 4,
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
                    viewModel.gameProgressDao.deleteProgress("Medium")
                }
            },
            onDismiss = {
                navController.navigateUp()
            })
    }

    Scaffold(modifier = Modifier.fillMaxSize().background(
            MaterialTheme.colorScheme.background
        ), topBar = {
        GameTopBar(title = "Medium Level ${gameState.currentLevel}") {
            if (gameState.isGameStarted) {
                showExitDialog = true
                isTimerPaused = true
            } else {
                navController.navigateUp()
            }
        }
    }) { padding ->
        GameBackground()
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
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

            // Color grid with smaller padding for 4x4
            ColorGrid(gridSize = gameState.gridSize,
                colorBoxes = mutableColorBoxes,
                showInitialColors = showInitialColors,
                onBoxClick = { index ->
                    if (!showInitialColors && gameState.isGameStarted && !mutableColorBoxes[index].isMatched) {
                        handleBoxSelection(index)
                    }
                })
        }
    }

    // Add GameExitDialog
    if (showExitDialog) {
        GameExitDialog(onDismiss = {
            showExitDialog = false
            isTimerPaused = false  // Resume from current timeLeft
        }, onConfirm = {
            // if (isSoundEnabled) soundManager.playButtonClick()
            navController.navigateUp()
        })
    }

    // Add auto-start LaunchedEffect
    LaunchedEffect(showGameOverDialog, showResumeDialog) {
        if (!showGameOverDialog && !showResumeDialog && !showExitDialog) {
            delay(500) // Small delay to ensure dialogs are fully hidden
            showInitialColors = true
        }
    }
}