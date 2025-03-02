package dev.toufikforyou.colormatching

import android.app.Application
import dev.toufikforyou.colormatching.main.di.initKoin
import org.koin.android.ext.koin.androidContext

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MyApplication)
        }
    }
} 