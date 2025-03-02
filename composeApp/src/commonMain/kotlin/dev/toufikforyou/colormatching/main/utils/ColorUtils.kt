package dev.toufikforyou.colormatching.main.utils

import androidx.compose.ui.graphics.Color
import dev.toufikforyou.colormatching.main.domain.model.ColorBox
import kotlin.math.sqrt
import kotlin.random.Random

fun generateColorPairs(gridSize: Int): List<ColorBox> {
    val totalBoxes = gridSize * gridSize
    val isEvenGrid = gridSize % 2 == 0

    // For even grids (like 4x4), use different pair calculation
    val pairs = if (isEvenGrid) {
        (totalBoxes) / 2
    } else {
        (totalBoxes - 1) / 2
    }

    val colors = generateRandomColors(pairs)
    val allColors = (colors + colors).shuffled()

    // Only create ignored position for odd-sized grids (3x3, 5x5)
    val ignoredPosition = if (!isEvenGrid) Random.nextInt(totalBoxes) else -1

    return List(totalBoxes) { index ->
        if (index == ignoredPosition) {
            ColorBox(color = Color.Transparent)
        } else {
            // For even grids, use direct index mapping
            val colorIndex = if (isEvenGrid) {
                index
            } else {
                if (index < ignoredPosition) index else index - 1
            }
            ColorBox(color = allColors[colorIndex])
        }
    }
}

private fun generateRandomColors(count: Int): List<Color> {
    val colors = mutableListOf<Color>()
    val similarityThreshold = 0.1f // Threshold for color similarity

    repeat(count) {
        var newColor: Color
        var isSimilar: Boolean

        // Generate colors until we find one that's not too similar
        do {
            newColor = Color(
                red = Random.nextFloat().coerceIn(0.2f, 0.8f), // Keep colors in mid-range
                green = Random.nextFloat().coerceIn(0.2f, 0.8f),
                blue = Random.nextFloat().coerceIn(0.2f, 0.8f),
                alpha = 1f
            )

            // Check similarity against existing colors using simple distance formula
            isSimilar = colors.any { color ->
                val redDiff = color.red - newColor.red
                val greenDiff = color.green - newColor.green
                val blueDiff = color.blue - newColor.blue
                sqrt((redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff).toDouble()) < similarityThreshold
            }
        } while (isSimilar) // Keep trying if color is too similar

        colors.add(newColor)
    }

    return colors
}