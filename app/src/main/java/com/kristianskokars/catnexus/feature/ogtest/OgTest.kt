package com.kristianskokars.catnexus.feature.ogtest

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.animateBounds
import com.skydoves.orbital.rememberMovableContentOf

enum class Screen {
    A, B;
}

@Composable
fun ScreenTransitionSample2(
    cat: Cat = Cat("3", "https://cdn2.thecatapi.com/images/2m1.jpg", null, System.currentTimeMillis()),
    imageLoader: ImageLoader,
) {
    Orbital {
        var screen by rememberSaveable { mutableStateOf(Screen.A) }
        val sizeAnim = spring<IntSize>(stiffness = Spring.StiffnessLow)
        val positionAnim = spring<IntOffset>(stiffness = Spring.StiffnessLow)
        val image = rememberMovableContentOf {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cat.url)
                    .crossfade(true)
                    .build(),
                modifier = Modifier.animateBounds(
                    modifier = if (screen == Screen.A) {
                        Modifier.size(80.dp)
                    } else {
                        Modifier.size(240.dp)
                    },
                    debug = true,
                    sizeAnimationSpec = tween(1000),
                    positionAnimationSpec = tween(1000)
                ),
                contentDescription = null,
                imageLoader = imageLoader,
            )
        }

        val title = rememberMovableContentOf {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .animateBounds(
                        modifier = Modifier,
                        sizeAnimationSpec = sizeAnim,
                        positionAnimationSpec = positionAnim
                    ),
            ) {
                Text(
                    text = "Cat",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "It Cute",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        if (screen == Screen.A) {
            ScreenA(
                sharedContent = {
                    image()
                    title()
                }) {
                screen = Screen.B
            }
        } else {
            ScreenB(
                sharedContent = {
                    image()
                    title()
                }) {
                screen = Screen.A
            }
        }
    }
}

@Composable
private fun ScreenA(
    sharedContent: @Composable () -> Unit,
    navigateToScreenB: () -> Unit
) {
    Orbital {
        Row(modifier = Modifier
            .background(color = Color(0xFFffd7d7))
            .padding(top = 200.dp)
            .fillMaxSize()
            .clickable {
                navigateToScreenB.invoke()
            },
            horizontalArrangement = Arrangement.End) {
            sharedContent()
        }
    }
}

@Composable
private fun ScreenB(
    sharedContent: @Composable () -> Unit,
    navigateToScreenA: () -> Unit
) {
    Orbital {
        Column(modifier = Modifier
            .background(color = Color(0xFFe3ffd9))
            .fillMaxSize()
            .clickable {
                navigateToScreenA()
            },
            horizontalAlignment = Alignment.End) {
            sharedContent()
        }
    }
}
