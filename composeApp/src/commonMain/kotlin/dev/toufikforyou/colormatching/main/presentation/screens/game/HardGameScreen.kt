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
fun HardGameScreen(
    navController: NavController, /* soundManager: SoundManager, isSoundEnabled: Boolean */
) {
    val viewModel: GameViewModel = koinViewModel { parametersOf(5, "Hard") }
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

    // Calculate total pairs for 5x5 grid
    val totalPairs = remember(gameState.gridSize) {
        (gameState.gridSize * gameState.gridSize) / 2
    }

    // Streak and combo system
    var currentStreak by remember { mutableIntStateOf(0) }
    var maxStreak by remember { mutableIntStateOf(0) }
    var lastMatchTime by remember { mutableLongStateOf(0L) }

    // Collect high scores
    val highScores by viewModel.highScoreDao.getHighScoresByDifficulty("Hard")
        .collectAsState(initial = emptyList())

    // Add state for showing resume dialog
    var showResumeDialog by remember { mutableStateOf(false) }
    var savedProgress by remember { mutableStateOf<GameProgress?>(null) }

    // Check for saved progress when screen opens
    LaunchedEffect(Unit) {
        savedProgress = viewModel.gameProgressDao.getProgress("Hard").firstOrNull()
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
                            difficulty = "Hard",
                            level = gameState.currentLevel,
                            score = gameState.score
                        )
                    )
                }
            }
        }
    }

    // Handle box selection function with enhanced scoring
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

                        // Update streak
                        currentStreak++
                        maxStreak = maxOf(maxStreak, currentStreak)

                        // Calculate bonus points based on streak and time
                        val streakBonus = currentStreak * 5
                        val timeBonus = when {
                            timeDiff < 1500 -> 20 // Super quick match
                            timeDiff < 2500 -> 15 // Quick match
                            timeDiff < 3500 -> 10 // Medium speed
                            else -> 5             // Base points
                        }

                        lastMatchTime = currentTime
                        val newMatchedPairs = gameState.matchedPairs + 1
                        viewModel.updateGameState {
                            it.copy(
                                matchedPairs = newMatchedPairs,
                                score = it.score + timeBonus + streakBonus
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
                            currentStreak = 0
                            showInitialColors = true
                        }
                    }
                } else {
                    scope.launch {
                        delay(500)
                        mutableColorBoxes = mutableColorBoxes.mapIndexed { i, box ->
                            if (i == firstIndex || i == secondIndex) {
                                box.copy(isSelected = false)
                            } else box
                        }
                        selectedBoxes.clear()
                        currentStreak = 0
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
            delay(7000) // 7 seconds for hard mode
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
                score = gameState.score, level = gameState.currentLevel, difficulty = "Hard"
            )
        }

        GameOverDialog(score = gameState.score,
            matchedPairs = gameState.matchedPairs,
            totalPairs = totalPairs,
            difficulty = "Hard",
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
                currentStreak = 0
                maxStreak = 0
                showInitialColors = true
            },
            onBack = {
                navController.navigateUp()
            })
    }

    if (showExitDialog) {
        GameExitDialog(onDismiss = {
            showExitDialog = false
            isTimerPaused = false
        }, onConfirm = {
            // if (isSoundEnabled) soundManager.playButtonClick()
            navController.navigateUp()
        })
    }

    if (showResumeDialog && savedProgress != null) {
        ResumeGameDialog(difficulty = "Hard",
            level = savedProgress!!.level,
            score = savedProgress!!.score,
            onResume = {
                showResumeDialog = false
                viewModel.updateGameState {
                    it.copy(
                        gridSize = 5,
                        timeLimit = viewModel.calculateTimeLimit(savedProgress!!.level),
                        isGameStarted = false,
                        currentLevel = savedProgress!!.level,
                        score = savedProgress!!.score
                    )
                }
                timeLeft = gameState.timeLimit
                mutableColorBoxes = generateColorPairs(gameState.gridSize)
                currentStreak = 0
                maxStreak = 0
            },
            onNewGame = {
                showResumeDialog = false
                scope.launch {
                    viewModel.gameProgressDao.deleteProgress("Hard")
                }
            },
            onDismiss = {
                navController.navigateUp()
            })
    }

    LaunchedEffect(showGameOverDialog, showResumeDialog) {
        if (!showGameOverDialog && !showResumeDialog && !showExitDialog) {
            delay(500) // Small delay to ensure dialogs are fully hidden
            showInitialColors = true
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        topBar = {
            GameTopBar(title = "Hard Level ${gameState.currentLevel}") {
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
                AnimatedGameScore(title = "Score", value = gameState.score.toString())
                AnimatedGameScore("Streak", "$currentStreak/$maxStreak")
            }

            // Color grid with smaller padding for 5x5
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
}