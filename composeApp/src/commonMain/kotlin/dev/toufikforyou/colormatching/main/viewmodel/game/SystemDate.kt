package dev.toufikforyou.colormatching.main.viewmodel.game

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun getCurrentDateFormatted(): String {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    return currentDate.toString() // Returns in format "YYYY-MM-DD"
}
