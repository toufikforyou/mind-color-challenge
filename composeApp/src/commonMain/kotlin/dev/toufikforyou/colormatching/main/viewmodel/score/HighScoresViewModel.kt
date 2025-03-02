package dev.toufikforyou.colormatching.main.viewmodel.score

import androidx.lifecycle.ViewModel
import dev.toufikforyou.colormatching.main.data.local.dao.HighScoreDao
import dev.toufikforyou.colormatching.main.data.local.entity.HighScore
import kotlinx.coroutines.flow.Flow

class HighScoresViewModel(
    highScoreDao: HighScoreDao
) : ViewModel() {
    val highScores: Flow<List<HighScore>> = highScoreDao.getAllHighScores()
} 