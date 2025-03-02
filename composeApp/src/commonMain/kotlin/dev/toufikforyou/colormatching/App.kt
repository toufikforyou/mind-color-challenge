package dev.toufikforyou.colormatching

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import dev.toufikforyou.colormatching.main.navigation.MainNavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            MainNavGraph()
        }
    }
}