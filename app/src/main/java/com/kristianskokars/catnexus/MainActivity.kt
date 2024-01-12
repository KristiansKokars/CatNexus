package com.kristianskokars.catnexus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import coil.ImageLoader
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.kristianskokars.catnexus.core.presentation.components.BackgroundSurface
import com.kristianskokars.catnexus.feature.NavGraphs
import com.kristianskokars.catnexus.lib.screenSlideTransitionAnimations
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var imageLoader: ImageLoader

    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BackgroundSurface {
                DestinationsNavHost(
                    engine = rememberAnimatedNavHostEngine(
                        rootDefaultAnimations = screenSlideTransitionAnimations,
                    ),
                    dependenciesContainerBuilder = {
                        dependency(imageLoader)
                    },
                    navGraph = NavGraphs.root,
                )
            }
        }
    }
}
