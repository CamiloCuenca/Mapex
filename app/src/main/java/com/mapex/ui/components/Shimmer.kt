package com.mapex.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** M3 skeleton shimmer placeholder that pulses between surface-variant tones. */
@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    // M3 motion token: emphasis transition ~1 000 ms
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    Box(
        modifier = modifier.background(
            // M3 surface-variant token as shimmer colour
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
            shape = RoundedCornerShape(8.dp)
        )
    )
}
