package dev.toufikforyou.colormatching.main.presentation.components.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun GameTimeScore(title: String = "Time", timeLeft: Int) {
    Surface(
        modifier = Modifier
            .padding(all = 8.dp)
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title, style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold
                )
            )
            TimerDisplay(timeLeft)
        }
    }
}

@Composable
private fun TimerDisplay(timeLeft: Int) {
    val isLowTime = timeLeft <= 5  // Warning threshold for easy mode
    val color by animateColorAsState(
        targetValue = if (isLowTime) Color.Red else MaterialTheme.colorScheme.primary,
        animationSpec = tween(500)
    )

    var scale by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
        )
    )

    LaunchedEffect(timeLeft) {
        if (isLowTime) {
            scale = 1.2f
            delay(100)
            scale = 1f
        }
    }

    Text(
        text = timeLeft.toString(), style = MaterialTheme.typography.headlineMedium.copy(
            color = color, fontWeight = FontWeight.Bold
        ), modifier = Modifier.scale(animatedScale)
    )
}