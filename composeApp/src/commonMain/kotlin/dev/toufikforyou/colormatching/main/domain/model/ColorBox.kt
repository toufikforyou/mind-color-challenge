package dev.toufikforyou.colormatching.main.domain.model

import androidx.compose.ui.graphics.Color

data class ColorBox(
    val color: Color, val isSelected: Boolean = false, val isMatched: Boolean = false
)