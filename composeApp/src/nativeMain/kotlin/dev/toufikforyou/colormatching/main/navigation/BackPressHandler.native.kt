package dev.toufikforyou.colormatching.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

actual class BackPressHandler actual constructor() {
    actual fun onBackPressed(callback: () -> Unit) {

    }
}

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
}