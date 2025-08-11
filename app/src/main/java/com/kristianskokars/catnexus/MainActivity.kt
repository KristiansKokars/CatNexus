package com.kristianskokars.catnexus

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import coil.ImageLoader
import com.kristianskokars.catnexus.core.presentation.DefaultHazeStyle
import com.kristianskokars.catnexus.core.presentation.components.BackgroundSurface
import com.kristianskokars.catnexus.core.presentation.components.BelowTopBarDownloadToast
import com.kristianskokars.catnexus.lib.Navigator
import com.kristianskokars.catnexus.lib.Toaster
import com.kristianskokars.catnexus.lib.launchImmediate
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.haze.LocalHazeStyle
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var imageLoader: ImageLoader
    @Inject lateinit var toaster: Toaster
    @Inject lateinit var navigator: Navigator

    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalSharedTransitionApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter") // we are not using anything that needs the safety padding
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
            val engine = rememberNavHostEngine()
            val navController = engine.rememberNavController()
            val destinationsNavigator = navController.rememberDestinationsNavigator()

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
                            Navigator.Action.GoBack -> destinationsNavigator.navigateUp()
                            is Navigator.Action.Navigate -> destinationsNavigator.navigate(navAction.direction)
                        }
                    }
                }
            }

            BackgroundSurface {
                Scaffold(
                    snackbarHost = { BelowTopBarDownloadToast(hostState = snackbarHostState) },
                ) {
                    SharedTransitionLayout {
                        CompositionLocalProvider(LocalHazeStyle provides DefaultHazeStyle) {
                            DestinationsNavHost(
                                navController = navController,
                                dependenciesContainerBuilder = {
                                    dependency(imageLoader)
                                    dependency(this@SharedTransitionLayout)
                                },
                                navGraph = NavGraphs.main,
                            )
                        }
                    }
                }
            }
        }
    }
}
