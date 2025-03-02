package dev.toufikforyou.colormatching.main.di

import dev.toufikforyou.colormatching.main.data.local.GameDatabase
import dev.toufikforyou.colormatching.main.viewmodel.game.GameViewModel
import dev.toufikforyou.colormatching.main.viewmodel.score.HighScoresViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single { get<GameDatabase>().gameProgressDao() }
    single { get<GameDatabase>().highScoreDao() }

    viewModel {
        HighScoresViewModel(
            highScoreDao = get()
        )
    }

    viewModel { (gridSize: Int, difficulty: String) ->
        GameViewModel(
            gameProgressDao = get(),
            highScoreDao = get(),
            // notificationHelper = get(),
            initialGridSize = gridSize,
            difficulty = difficulty
        )
    }
}