package com.rafael.ibooks.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DotLoadingIndicator(
    modifier: Modifier = Modifier,
    dotColor: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dot_transition")
    val dotScale1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 300, delayMillis = 0),
            repeatMode = RepeatMode.Reverse
        ), label = "dot_scale_1"
    )
    val dotScale2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 300, delayMillis = 150),
            repeatMode = RepeatMode.Reverse
        ), label = "dot_scale_2"
    )
    val dotScale3 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 300, delayMillis = 300),
            repeatMode = RepeatMode.Reverse
        ), label = "dot_scale_3"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Dot(scale = dotScale1, color = dotColor)
        Spacer(modifier = Modifier.width(4.dp))
        Dot(scale = dotScale2, color = dotColor)
        Spacer(modifier = Modifier.width(4.dp))
        Dot(scale = dotScale3, color = dotColor)
    }
}

@Composable
private fun Dot(scale: Float, color: Color) {
    Canvas(modifier = Modifier.size(8.dp * scale)) {
        drawCircle(color = color)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDotLoadingIndicator() {
    MaterialTheme {
        DotLoadingIndicator()
    }
}