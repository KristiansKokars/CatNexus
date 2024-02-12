package com.kristianskokars.catnexus.core.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.launch

@Composable
fun ZoomableBox(
    modifier: Modifier = Modifier,
    minScale: Float = 1f,
    maxScale: Float = 3f,
    onScaleChange: (Float) -> Unit,
    content: @Composable ZoomableBoxScope.() -> Unit
) {
    val scale = remember { Animatable(1f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = scale.value) {
        onScaleChange(scale.value)
    }

    Box(
        modifier = modifier
            .clip(RectangleShape)
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    coroutineScope.launch {
                        scale.animateTo(maxOf(minScale, minOf(scale.targetValue * zoom, maxScale)))
                    }
                    val maxX = (size.width * (scale.targetValue - 1)) / 2
                    val minX = -maxX
                    coroutineScope.launch {
                        offsetX.animateTo(
                            maxOf(
                                minX,
                                minOf(maxX, offsetX.targetValue + pan.x)
                            ),
                            tween(0)
                        )
                    }
                    val maxY = (size.height * (scale.targetValue - 1)) / 4
                    val minY = -maxY
                    coroutineScope.launch {
                        offsetY.animateTo(
                            maxOf(
                                minY,
                                minOf(maxY, offsetY.targetValue + pan.y)
                            ),
                            tween(0)
                        )
                    }
                }
            }
    ) {
        val scope = ZoomableBoxScopeImpl(
            scale.value,
            offsetX.value,
            offsetY.value,
            zoomToDefault = {
                coroutineScope.launch { scale.animateTo(1f, tween(300)) }
                coroutineScope.launch { offsetX.animateTo(0f, tween(300)) }
                coroutineScope.launch { offsetY.animateTo(0f, tween(300)) }
            },
            this
        )
        scope.content()
    }
}

interface ZoomableBoxScope : BoxScope {
    val scale: Float
    val offsetX: Float
    val offsetY: Float
    val zoomToDefault: () -> Unit
}

private data class ZoomableBoxScopeImpl(
    override val scale: Float,
    override val offsetX: Float,
    override val offsetY: Float,
    override val zoomToDefault: () -> Unit,
    val boxScope: BoxScope,
) : ZoomableBoxScope, BoxScope by boxScope
