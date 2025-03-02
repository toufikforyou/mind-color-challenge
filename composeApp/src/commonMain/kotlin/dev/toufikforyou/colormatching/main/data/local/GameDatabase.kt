package dev.toufikforyou.colormatching.main.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.toufikforyou.colormatching.main.data.local.dao.GameProgressDao
import dev.toufikforyou.colormatching.main.data.local.dao.HighScoreDao
import dev.toufikforyou.colormatching.main.data.local.entity.GameProgress
import dev.toufikforyou.colormatching.main.data.local.entity.HighScore

@Database(
    entities = [GameProgress::class, HighScore::class], version = 1
)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameProgressDao(): GameProgressDao
    abstract fun highScoreDao(): HighScoreDao
}