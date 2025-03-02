package dev.toufikforyou.colormatching.main.presentation.components.game

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import dev.toufikforyou.colormatching.main.domain.model.ColorBox
import kotlinx.coroutines.delay
import mindcolorchallenge.composeapp.generated.resources.Res
import mindcolorchallenge.composeapp.generated.resources.roket_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun ColorGrid(
    gridSize: Int, colorBoxes: List<ColorBox>, showInitialColors: Boolean, onBoxClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridSize),
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(colorBoxes.size) { index ->
            val box = colorBoxes[index]
            ColorBox(
                color = if (showInitialColors || box.isSelected || box.isMatched) box.color
                else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.05f),
                isMatched = box.isMatched,
                onClick = { onBoxClick(index) },
                modifier = Modifier.aspectRatio(1f).animateContentSize().border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                )
            )
        }
    }
}

@Composable
private fun ColorBox(
    color: Color, isMatched: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
        )
    )

    LaunchedEffect(isMatched) {
        if (isMatched) {
            scale = 1.2f
            delay(100)
            scale = 1f
        }
    }

    Surface(
        modifier = modifier.scale(animatedScale).animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        color = color,
        border = if (isMatched) BorderStroke(
            2.dp, MaterialTheme.colorScheme.primaryContainer
        ) else null,
        onClick = onClick
    ) {
        if (isMatched) {
            Box(
                modifier = Modifier.fillMaxSize(0.8f), contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        Res.drawable.roket_icon
                    ), contentDescription = null, modifier = Modifier.scale(animatedScale)
                )
            }
        }
    }
} 