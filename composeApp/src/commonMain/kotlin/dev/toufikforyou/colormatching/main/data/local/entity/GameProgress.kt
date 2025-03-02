package dev.toufikforyou.colormatching.main.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_progress")
data class GameProgress(
    @PrimaryKey val difficulty: String,  // "Easy", "Medium", or "Hard"
    val level: Int,

    @ColumnInfo(name = "score")
    val score: Int,

    @ColumnInfo(name = "streak")
    val streak: Int = 0,

    @ColumnInfo(name = "last_played")
    val lastPlayed: Long = 0L
) 