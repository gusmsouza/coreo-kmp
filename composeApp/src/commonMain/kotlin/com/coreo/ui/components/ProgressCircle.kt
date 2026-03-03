package com.coreo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.coreo.theme.CoreoColors

@Composable
fun ProgressCircle(
    progress: Float, // 0.0 to 1.0
    modifier: Modifier = Modifier,
    color: Color = CoreoColors.Primary,
    strokeWidth: Dp = 12.dp
) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(
            width = strokeWidth.toPx(),
            cap   = StrokeCap.Round
        )
        val inset      = strokeWidth.toPx() / 2f
        val arcSize    = Size(size.width - inset * 2, size.height - inset * 2)
        val topLeft    = Offset(inset, inset)

        // Background track
        drawArc(
            color       = CoreoColors.PrimaryLight,
            startAngle  = -90f,
            sweepAngle  = 360f,
            useCenter   = false,
            topLeft     = topLeft,
            size        = arcSize,
            style       = Stroke(width = strokeWidth.toPx())
        )

        // Progress arc
        drawArc(
            color      = color,
            startAngle = -90f,
            sweepAngle = 360f * progress.coerceIn(0f, 1f),
            useCenter  = false,
            topLeft    = topLeft,
            size       = arcSize,
            style      = stroke
        )
    }
}