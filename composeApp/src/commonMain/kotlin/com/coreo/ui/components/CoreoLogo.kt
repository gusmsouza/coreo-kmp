package com.coreo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.coreo.theme.CoreoColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CoreoLogo(size: Dp = 60.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val radius = minOf(this.size.width, this.size.height) / 2f

        // Organic hexagon variations (mirrors HexagonShape in Swift)
        val variations = listOf(0.98f, 1.02f, 0.99f, 1.01f, 0.98f, 1.0f)

        val path = Path()
        variations.forEachIndexed { index, variation ->
            val angle = index * (Math.PI / 3) - Math.PI / 2
            val r = radius * variation
            val x = cx + cos(angle).toFloat() * r
            val y = cy + sin(angle).toFloat() * r
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        // Draw Forest Green hexagon
        drawPath(path = path, color = CoreoColors.Primary)

        // Draw Amber circle center (30% of size)
        val circleRadius = radius * 0.3f
        drawCircle(
            color  = CoreoColors.Accent,
            radius = circleRadius,
            center = Offset(cx, cy)
        )
    }
}