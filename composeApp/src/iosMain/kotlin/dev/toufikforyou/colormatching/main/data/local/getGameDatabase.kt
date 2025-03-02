package dev.toufikforyou.colormatching.main.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

fun getGameDatabase(): GameDatabase {
    val dbFile = NSHomeDirectory() + "/game.db"

    return Room.databaseBuilder<GameDatabase>(name = dbFile).setDriver(BundledSQLiteDriver())
        .build()
}