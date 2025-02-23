package dev.toufikforyou.colormatching

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Text(
            text = "Hello, World!",
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}