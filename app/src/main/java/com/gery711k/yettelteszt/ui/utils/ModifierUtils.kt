package com.gery711k.yettelteszt.ui.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Modifier.rotatingBorderAnimation(
    isLoading: Boolean,
    shape: Shape,
    borderStroke: BorderStroke = BorderStroke(
        width = 2.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF7B68EE),
                Color(0xFF00CED1),
                Color(0xFFFF6B6B),
            )
        )
    ),
) = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "rotating border")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    if (isLoading) {
        this
            .clip(shape)
            .drawBehind {
                rotate(rotation) {
                    drawCircle(
                        brush = borderStroke.brush,
                        radius = size.width,
                    )
                }
            }
            .padding(borderStroke.width)
    } else {
        this.padding(borderStroke.width)
    }
}

@Composable
fun Modifier.shimmerLoadingAnimation(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.02f),
            Color.LightGray.copy(alpha = 0.2f),
        ),
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = 0f)
    )

    drawBehind {
        drawRect(brush)
    }
}

@Composable
fun Modifier.skipInPreview(
    modifier: @Composable Modifier.() -> Modifier,
): Modifier = this.then(
    if (LocalInspectionMode.current) {
        Modifier
    } else Modifier.modifier()
)