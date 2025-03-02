package dev.toufikforyou.colormatching.main.navigation

import androidx.compose.runtime.Composable

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class BackPressHandler() {
    fun onBackPressed(callback: () -> Unit)
}

@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)