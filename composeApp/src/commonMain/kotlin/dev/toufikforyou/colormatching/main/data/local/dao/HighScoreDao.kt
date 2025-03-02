package dev.toufikforyou.colormatching.main.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.toufikforyou.colormatching.main.data.local.entity.HighScore
import kotlinx.coroutines.flow.Flow

@Dao
interface HighScoreDao {
    @Query("SELECT * FROM high_scores ORDER BY score DESC LIMIT 10")
    fun getAllHighScores(): Flow<List<HighScore>>

    @Query("SELECT * FROM high_scores WHERE difficulty = :difficulty ORDER BY score DESC LIMIT 5")
    fun getHighScoresByDifficulty(difficulty: String): Flow<List<HighScore>>

    @Insert
    suspend fun insertHighScore(highScore: HighScore)

    @Query("DELETE FROM high_scores WHERE difficulty = :difficulty AND score < :score AND id NOT IN (SELECT id FROM high_scores WHERE difficulty = :difficulty ORDER BY score DESC LIMIT 5)")
    suspend fun deleteOldScores(difficulty: String, score: Int)

    @Query("SELECT COUNT(*) < 5 OR :score > (SELECT MIN(score) FROM high_scores WHERE difficulty = :difficulty) FROM high_scores WHERE difficulty = :difficulty")
    suspend fun isHighScore(difficulty: String, score: Int): Boolean
}