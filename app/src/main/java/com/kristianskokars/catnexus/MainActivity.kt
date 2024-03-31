package com.kristianskokars.catnexus

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import coil.ImageLoader
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.kristianskokars.catnexus.core.presentation.components.BackgroundSurface
import com.kristianskokars.catnexus.core.presentation.components.BelowTopBarDownloadToast
import com.kristianskokars.catnexus.feature.NavGraphs
import com.kristianskokars.catnexus.lib.Navigator
import com.kristianskokars.catnexus.lib.Toaster
import com.kristianskokars.catnexus.lib.launchImmediate
import com.kristianskokars.catnexus.lib.screenSlideTransitionAnimations
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var imageLoader: ImageLoader
    @Inject lateinit var toaster: Toaster
    @Inject lateinit var navigator: Navigator

    private val viewModel by viewModels<MainViewModel>()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter") // we are not using anything that needs the safety padding
    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition { !viewModel.isInitialized.value }
        }
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(resources.getColor(R.color.black, null))
        )

        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val context = LocalContext.current
            val engine = rememberAnimatedNavHostEngine(
                rootDefaultAnimations = screenSlideTransitionAnimations,
            )
            val navController = engine.rememberNavController()

            LaunchedEffect(key1 = Unit) {
                launchImmediate {
                    toaster.messages.collect { message ->
                        snackbarHostState.showSnackbar(message.text.get(context))
                    }
                }
            }

            LaunchedEffect(key1 = Unit) {
                launchImmediate {
                    navigator.navigationActions.collect { navAction ->
                        when (navAction) {
                            Navigator.Action.GoBack -> navController.navigateUp()
                            is Navigator.Action.Navigate -> navController.navigate(navAction.direction)
                        }
                    }
                }
            }

            BackgroundSurface {
                Scaffold(
                    snackbarHost = { BelowTopBarDownloadToast(hostState = snackbarHostState) }
                ) {
                    DestinationsNavHost(
                        engine = engine,
                        navController = navController,
                        dependenciesContainerBuilder = {
                            dependency(imageLoader)
                        },
                        navGraph = NavGraphs.root,
                    )
                }
            }
        }
    }
}
