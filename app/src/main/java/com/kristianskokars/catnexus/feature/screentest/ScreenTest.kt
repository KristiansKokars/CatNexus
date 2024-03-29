package com.kristianskokars.catnexus.feature.screentest

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.animateBounds
import com.skydoves.orbital.rememberMovableContentOf

sealed class Screen {
    data class A(val catId: String? = null, val cat: (@Composable () -> Unit)? = null) : Screen()
    data class B(val cat: Cat, val catCard: @Composable () -> Unit) : Screen()
}

@Composable
fun ScreenTransitionSample(
    imageLoader: ImageLoader,
) {
    Orbital {
        var screen by remember { mutableStateOf<Screen>(Screen.A()) }

        when (val currentScreen = screen) {
            is Screen.A -> ScreenA(
                screen = currentScreen,
                imageLoader = imageLoader,
                sharedContent = { catId, catCard ->
                    if (catId == currentScreen.catId) {
                        currentScreen.cat?.let { it() }
                    } else {
                        catCard()
                    }
                },
                navigateToScreenB = { cat, catCard ->
                    screen = Screen.B(cat, catCard)
                }
            )
            is Screen.B -> ScreenB(
                sharedContent = {
                    currentScreen.catCard()
                },
                navigateToScreenA = { catId, catCard ->
                    screen = Screen.A(catId, catCard)
                },
                cat = currentScreen.cat
            )
        }

    }
}

private val cats = listOf(
    Cat("1", "https://cdn2.thecatapi.com/images/c8e.jpg", null, System.currentTimeMillis()),
    Cat("2", "https://cdn2.thecatapi.com/images/MTQ5NzIzMw.jpg", null, System.currentTimeMillis()),
    Cat("3", "https://cdn2.thecatapi.com/images/2m1.jpg", null, System.currentTimeMillis())
)

@Composable
private fun ScreenA(
    screen: Screen,
    imageLoader: ImageLoader,
    sharedContent: @Composable (catId: String?, @Composable () -> Unit) -> Unit,
    navigateToScreenB: (cat: Cat, catCard: @Composable () -> Unit) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(200.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(cats, key = { it.id }) { cat ->
            val image = rememberMovableContentOf {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(cat.url)
                        .crossfade(true)
                        .build(),
                    modifier = Modifier.animateBounds(
                        modifier = Modifier.fillMaxSize(),
                        sizeAnimationSpec = tween(400),
                        positionAnimationSpec = tween(400)
                    ),
                    contentDescription = null,
                    imageLoader = imageLoader,
                )
            }

            Row(
                modifier = Modifier
                    .clickable { navigateToScreenB(cat, image) }
            ) {
                sharedContent(cat.id, image)
            }
        }
    }
}

@Composable
private fun ScreenB(
    cat: Cat,
    sharedContent: @Composable () -> Unit,
    navigateToScreenA: (catId: String, cat: @Composable () -> Unit) -> Unit
) {
    BackHandler {
        navigateToScreenA(cat.id, sharedContent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                navigateToScreenA(cat.id, sharedContent)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        sharedContent()
    }
}
