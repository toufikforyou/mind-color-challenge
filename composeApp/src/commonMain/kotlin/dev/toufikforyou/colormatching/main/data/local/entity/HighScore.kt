package dev.toufikforyou.colormatching.main.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "high_scores")
data class HighScore(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val score: Int,
    val level: Int,
    val difficulty: String,
    val date: String
) 