package dev.toufikforyou.colormatching.main.di

import dev.toufikforyou.colormatching.dependency.DbClient
import dev.toufikforyou.colormatching.main.data.local.getGameDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::DbClient)
    single {
        getGameDatabase(get())
    }
}