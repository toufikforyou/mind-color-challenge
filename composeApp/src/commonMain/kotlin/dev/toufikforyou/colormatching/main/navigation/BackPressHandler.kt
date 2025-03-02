package dev.toufikforyou.colormatching.main.navigation

import androidx.compose.runtime.Composable

expect class BackPressHandler() {
    fun onBackPressed(callback: () -> Unit)
}

@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)