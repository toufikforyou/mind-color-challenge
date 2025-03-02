package dev.toufikforyou.colormatching

import androidx.compose.ui.window.ComposeUIViewController
import dev.toufikforyou.colormatching.main.di.initKoin

fun MainViewController() = ComposeUIViewController(configure = {
    initKoin()
}) { App() }