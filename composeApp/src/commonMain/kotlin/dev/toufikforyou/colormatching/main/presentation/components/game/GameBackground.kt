@file:Suppress("SameParameterValue")

package dev.toufikforyou.colormatching.main.presentation.components.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun GameBackground() {
    Box(
        modifier = Modifier.fillMaxSize().blur(20.dp)
    ) {
        // Animated gradient background
        GradientBackground()

        // Floating particles in multiple layers
        repeat(3) { layer ->
            ParticleLayer(
                particleCount = 8, alpha = when (layer) {
                    0 -> 0.15f
                    1 -> 0.10f
                    else -> 0.05f
                }, speedMultiplier = when (layer) {
                    0 -> 1f
                    1 -> 0.7f
                    else -> 0.4f
                }
            )
        }

        // Animated wave effect
        WaveEffect()

        // Animated geometric patterns
        GeometricPatterns()

        // Floating orbs
        FloatingOrbs()
    }
}

@Composable
private fun GradientBackground() {
    val colors = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
    )

    var currentColorIndex by remember { mutableIntStateOf(0) }
    val animatedColor by animateColorAsState(
        targetValue = colors[currentColorIndex], animationSpec = tween(3000)
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentColorIndex = (currentColorIndex + 1) % colors.size
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(animatedColor)
    )
}

@Composable
private fun WaveEffect() {
    val waveColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * 3.1416).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing), repeatMode = RepeatMode.Restart
        ),
        label = "wave phase"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val wavePath = Path()
        val height = size.height
        val width = size.width

        wavePath.moveTo(0f, height / 2)

        for (x in 0..width.toInt() step 10) {
            val y = (height / 2) + (50 * kotlin.math.sin(x / 100f + wavePhase))
            wavePath.lineTo(x.toFloat(), y)
        }

        drawPath(
            path = wavePath, color = waveColor
        )
    }
}

@Composable
private fun GeometricPatterns() {
    val lineColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
    val rotation by rememberInfiniteTransition(label = "rotation").animateFloat(
        initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing)
        ), label = "pattern rotation"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        rotate(rotation) {
            // Draw hexagonal pattern
            repeat(6) { i ->
                val angle = (i * 60f) * (3.1416 / 180f)
                val radius = size.minDimension / 4
                val x = center.x + (radius * kotlin.math.cos(angle)).toFloat()
                val y = center.y + (radius * kotlin.math.sin(angle)).toFloat()

                drawLine(
                    color = lineColor, start = center, end = Offset(x, y), strokeWidth = 5f
                )
            }
        }
    }
}

@Composable
private fun ParticleLayer(
    particleCount: Int, alpha: Float, speedMultiplier: Float
) {
    repeat(particleCount) {
        AnimatedParticle(
            alpha = alpha, speedMultiplier = speedMultiplier
        )
    }
}

@Composable
private fun AnimatedParticle(
    alpha: Float, speedMultiplier: Float
) {
    val offset = rememberFloatingParticle(speedMultiplier)
    val size = remember { Random.nextInt(8, 25).dp }

    // Animate particle properties
    var scale by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale, animationSpec = infiniteRepeatable(
            animation = tween(2000), repeatMode = RepeatMode.Reverse
        )
    )

    // Animate color
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary
    )
    var colorIndex by remember { mutableIntStateOf(0) }
    val animatedColor by animateColorAsState(
        targetValue = colors[colorIndex], animationSpec = tween(3000)
    )

    // Periodically change color
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            colorIndex = (colorIndex + 1) % colors.size
        }
    }

    // Periodically animate scale
    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(2000, 4000))
            scale = Random.nextFloat() * (1.3f - 0.7f) + 0.7f
        }
    }

    Box(modifier = Modifier.offset(
            x = offset.x * 300.dp, y = offset.y * 500.dp
        ).size(size).scale(animatedScale).graphicsLayer {
            rotationZ = offset.x * 360 // Rotate based on movement
        }.background(
            color = animatedColor.copy(alpha = alpha), shape = CircleShape
        ))
}

@Composable
private fun rememberFloatingParticle(speedMultiplier: Float): Offset {
    val x = remember { Animatable(0f) }
    val y = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        x.animateTo(
            targetValue = 1f, animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (3000 / speedMultiplier).toInt(), easing = LinearEasing
                ), repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        y.animateTo(
            targetValue = 1f, animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (2000 / speedMultiplier).toInt(), easing = LinearEasing
                ), repeatMode = RepeatMode.Reverse
            )
        )
    }

    return Offset(x.value, y.value)
}

@Composable
private fun FloatingOrbs() {
    // Get theme colors in composable context
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    val infiniteTransition = rememberInfiniteTransition(label = "orbs")

    // Orb 1 animations
    val orb1X by infiniteTransition.animateFloat(
        initialValue = -0.2f, targetValue = 1.2f, animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing), repeatMode = RepeatMode.Restart
        ), label = "orb1_x"
    )

    val orb1Y by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f, animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        ), label = "orb1_y"
    )

    // Orb 2 animations
    val orb2X by infiniteTransition.animateFloat(
        initialValue = 1.2f, targetValue = -0.2f, animationSpec = infiniteRepeatable(
            animation = tween(17000, easing = LinearEasing), repeatMode = RepeatMode.Restart
        ), label = "orb2_x"
    )

    val orb2Y by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.2f, animationSpec = infiniteRepeatable(
            animation = tween(11000, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        ), label = "orb2_y"
    )

    // Scale animation for both orbs
    val orbScale by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f, animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        ), label = "orb_scale"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Use pre-fetched colors from composable context
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.15f),
                    primaryColor.copy(alpha = 0.05f),
                    Color.Transparent
                ), radius = size.maxDimension * 0.4f
            ), radius = size.maxDimension * 0.3f, center = Offset(
                x = size.width * orb1X, y = size.height * orb1Y
            ), alpha = 0.4f
        )

        // Orb 2 - Secondary color
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    secondaryColor.copy(alpha = 0.15f),
                    secondaryColor.copy(alpha = 0.05f),
                    Color.Transparent
                ), radius = size.maxDimension * 0.35f
            ), radius = size.maxDimension * 0.25f, center = Offset(
                x = size.width * orb2X, y = size.height * orb2Y
            ), alpha = 0.4f
        )

        // Central glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    tertiaryColor.copy(alpha = 0.1f), Color.Transparent
                ), center = center, radius = size.minDimension * 0.5f * orbScale
            ), radius = size.minDimension * 0.5f * orbScale, center = center, alpha = 0.2f
        )
    }
} 