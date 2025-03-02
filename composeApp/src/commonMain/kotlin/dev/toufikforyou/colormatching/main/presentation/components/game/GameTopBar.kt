package dev.toufikforyou.colormatching.main.presentation.components.game

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTopBar(title: String = "Mind Color Challenge", onNavigateBack: () -> Unit) {
    TopAppBar(title = {
        Text(
            title, style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold
            )
        )
    }, navigationIcon = {
        IconButton(onClick = onNavigateBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }, colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent
    )
    )
}