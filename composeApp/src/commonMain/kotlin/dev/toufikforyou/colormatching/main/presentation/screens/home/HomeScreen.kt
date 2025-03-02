package dev.toufikforyou.colormatching.main.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.toufikforyou.colormatching.main.navigation.Screen
import dev.toufikforyou.colormatching.main.presentation.components.game.GameBackground
import dev.toufikforyou.colormatching.main.presentation.components.home.LargeMenuButton
import dev.toufikforyou.colormatching.main.presentation.components.home.SmallMenuButton
import mindcolorchallenge.composeapp.generated.resources.Res
import mindcolorchallenge.composeapp.generated.resources.color_matching_game_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreen(
    navController: NavController,
    /*
    soundManager: SoundManager, isSoundEnabled: Boolean
    */
) {
    val scrollState = rememberScrollState()

    Scaffold { padding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            GameBackground()

            Column(
                modifier = Modifier.fillMaxWidth().padding(padding).verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                // Replace old AnimatedLogo with new animated logo
                Image(
                    painter = painterResource(Res.drawable.color_matching_game_icon),
                    contentDescription = "Game Logo",
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Start Game Button (Highlighted)
                "Begin your color matching journey".LargeMenuButton(
                    title = "Start Game",
                    icon = Icons.Filled.PlayArrow,
                    onClick = {
                        // if (isSoundEnabled) soundManager.playButtonClick()
                        navController.navigate(Screen.Difficulty)
                    })

                Spacer(modifier = Modifier.height(32.dp))

                // Other Menu Items in a Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SmallMenuButton(icon = Icons.Filled.MailOutline, label = "Scores", onClick = {
                        // if (isSoundEnabled) soundManager.playButtonClick()
                        navController.navigate(Screen.HighScores)
                    })

                    SmallMenuButton(icon = Icons.Filled.Settings, label = "Settings", onClick = {
                        // if (isSoundEnabled) soundManager.playButtonClick()
                        navController.navigate(Screen.Settings)
                    })

                    SmallMenuButton(icon = Icons.Filled.Info, label = "Guide", onClick = {
                        // if (isSoundEnabled) soundManager.playButtonClick()
                        navController.navigate(Screen.Guide)
                    })
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "v1.0.3",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
