package com.vraj.trackmyscore.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vraj.trackmyscore.ui.theme.GradientDeep
import com.vraj.trackmyscore.ui.theme.TrackMyScoreTheme
import com.vraj.trackmyscore.util.MainScreen
import com.vraj.trackmyscore.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrackMyScoreTheme {
                val navHostController = rememberNavController()
                val viewModel: MainViewModel = hiltViewModel()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(GradientDeep))
                        .padding(15.dp)
                ) {
                    MainScreens(
                        navHostController = navHostController,
                        viewModel = viewModel,
                        modifier = Modifier.padding(bottom = 40.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun MainScreens(
        navHostController: NavHostController,
        viewModel: MainViewModel,
        modifier: Modifier = Modifier
    ) {
        NavHost(
            navController = navHostController,
            startDestination = MainScreen.PlayersScreen.route,
            modifier = modifier
        ) {
            composable(MainScreen.PlayersScreen.route) {
                PlayersScreen(navHostController, viewModel)
            }
            composable(MainScreen.LeaderboardScreen.route) {
                LeaderboardScreen(navHostController, viewModel)
            }
            composable(MainScreen.GameScreen.route) {
                GameScreen(navHostController, viewModel)
            }
            composable(MainScreen.StatsScreen.route) {
                StatsScreen(navHostController, viewModel)
            }
        }
    }
}