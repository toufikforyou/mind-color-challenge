package dev.toufikforyou.colormatching.main.navigation

// In androidMain
import android.annotation.SuppressLint
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class BackPressHandler {
    actual fun onBackPressed(callback: () -> Unit) {
        // No @Composable annotation here, we'll use a different approach
    }

    // Add a composable function to handle the back press
    @SuppressLint("NotConstructor")
    @Composable
    fun BackPressHandler(enabled: Boolean = true, onBackPressed: () -> Unit) {
        val context = LocalContext.current
        val activity = context as? androidx.activity.ComponentActivity

        DisposableEffect(activity, enabled) {
            if (activity == null || !enabled) return@DisposableEffect onDispose {}

            val backCallback = object : OnBackPressedCallback(enabled) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            }

            activity.onBackPressedDispatcher.addCallback(backCallback)

            onDispose {
                backCallback.remove()
            }
        }
    }
}

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    val backHandler = remember { BackPressHandler() }
    backHandler.BackPressHandler(enabled, onBack)
}