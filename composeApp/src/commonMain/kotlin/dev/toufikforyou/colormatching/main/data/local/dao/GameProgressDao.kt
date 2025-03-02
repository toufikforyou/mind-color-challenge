package dev.toufikforyou.colormatching.main.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.toufikforyou.colormatching.main.data.local.entity.GameProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface GameProgressDao {
    @Query("SELECT * FROM game_progress WHERE difficulty = :difficulty")
    fun getProgress(difficulty: String): Flow<GameProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: GameProgress)

    @Query("DELETE FROM game_progress WHERE difficulty = :difficulty")
    suspend fun deleteProgress(difficulty: String)
} 