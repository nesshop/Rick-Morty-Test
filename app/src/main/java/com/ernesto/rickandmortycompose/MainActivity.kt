package com.ernesto.rickandmortycompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ernesto.rickandmortycompose.core.navigation.NavigationWrapper
import com.ernesto.rickandmortycompose.designsystem.components.atoms.RickAndMortyText
import com.ernesto.rickandmortycompose.designsystem.theme.LightAqua
import com.ernesto.rickandmortycompose.designsystem.theme.MediumGreen
import com.ernesto.rickandmortycompose.designsystem.theme.RickAndMortyComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            RickAndMortyComposeTheme {
                MainContent()
            }
        }
    }

    @Composable
    private fun MainContent() {
        val navController = rememberNavController()
        val navStackEntry by navController.currentBackStackEntryAsState()
        val isDetailScreen =
            navStackEntry?.destination?.route?.contains("CharacterDetail") == true

        var topBarActions by remember {
            mutableStateOf<(@Composable RowScope.() -> Unit)?>(
                null
            )
        }

        val animatedColor by animateColorAsState(
            targetValue = if (isDetailScreen) MediumGreen else LightAqua
        )

        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val title =
                        if (isDetailScreen) stringResource(R.string.character_detail_screen_title)
                        else stringResource(R.string.character_list_screen_title)
                    RickAndMortyText(
                        text = title,
                        color = animatedColor,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    if (isDetailScreen) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    topBarActions?.invoke(this)
                }
            )
        }) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavigationWrapper(
                    navController,
                    onSetTopBarActions = { actions ->
                        topBarActions = actions
                    })
            }
        }
    }
}