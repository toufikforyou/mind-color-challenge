package dev.toufikforyou.colormatching.main.presentation.components.difficulty

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DifficultyButton(
    text: String, description: String, containerColor: Color, onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }

    // Button scale animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
        )
    )

    // Floating animation
    val floatOffset by animateFloatAsState(
        targetValue = if (isHovered) -8f else 0f, animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        )
    )

    // Glow animation
    val buttonGlow by animateFloatAsState(
        targetValue = if (isPressed) 0f else if (isHovered) 1f else 0.5f, animationSpec = tween(500)
    )

    // Text scale animation
    val textScale by animateFloatAsState(
        targetValue = if (isHovered) 1.1f else 1f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
        )
    )

    Box(modifier = Modifier
        .width(280.dp)
        .offset(y = floatOffset.dp)
        .scale(scale)
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    when (event.type) {
                        PointerEventType.Enter -> isHovered = true
                        PointerEventType.Exit -> isHovered = false
                        else -> { /* do nothing */
                        }
                    }
                }
            }
        }) {
        // Glow effect
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(buttonGlow),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        ) {
            Spacer(modifier = Modifier.height(88.dp))
        }

        // Main button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(24.dp)
                )
                .pointerInput(Unit) {
                    detectTapGestures(onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    })
                },
            shape = RoundedCornerShape(24.dp),
            color = containerColor,
            shadowElevation = if (isPressed) 0.dp else 8.dp
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = text,
                        modifier = Modifier.scale(textScale),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description, style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    )
                }

                // Animated icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(
                                alpha = if (isHovered) 0.2f else 0.1f
                            )
                        ), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                            .scale(if (isHovered) 1.2f else 1f),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}