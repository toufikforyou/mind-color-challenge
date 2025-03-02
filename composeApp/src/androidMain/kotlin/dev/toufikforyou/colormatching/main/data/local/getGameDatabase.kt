package dev.toufikforyou.colormatching.main.data.local

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun getGameDatabase(context: Context): GameDatabase {
    val dbFile = context.getDatabasePath("game.db")

    return Room.databaseBuilder<GameDatabase>(
        context = context.applicationContext, name = dbFile.absolutePath
    ).setDriver(BundledSQLiteDriver()).build()
}