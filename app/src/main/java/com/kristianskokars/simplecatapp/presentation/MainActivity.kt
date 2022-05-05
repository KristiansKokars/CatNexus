package com.kristianskokars.simplecatapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.kristianskokars.simplecatapp.presentation.cat_list.CatListScreen
import com.kristianskokars.simplecatapp.presentation.components.BackgroundSurface
import com.kristianskokars.simplecatapp.presentation.ui.theme.SimpleCatAppTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BackgroundSurface {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}
