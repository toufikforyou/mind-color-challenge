package dev.toufikforyou.colormatching.main.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_progress")
data class GameProgress(
    @PrimaryKey val difficulty: String,  // "Easy", "Medium", or "Hard"
    val level: Int,
    val score: Int,
    val streak: Int = 0,
    val lastPlayed: Long = 0L
) 