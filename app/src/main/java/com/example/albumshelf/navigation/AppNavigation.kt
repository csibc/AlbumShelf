package com.example.albumshelf.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.albumshelf.AlbumShelfApp
import com.example.albumshelf.ui.screens.add.AddScreen
import com.example.albumshelf.ui.screens.detail.DetailScreen
import com.example.albumshelf.ui.screens.library.LibraryScreen
import com.example.albumshelf.ui.screens.recommendations.RecommendationsScreen
import com.example.albumshelf.ui.screens.search.SearchScreen
import com.example.albumshelf.ui.screens.welcome.WelcomeScreen

@Composable
fun AppNavigation(app: AlbumShelfApp) {
    val navController = rememberNavController()
    val animationDuration = 300

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,

        enterTransition = { fadeIn(animationSpec = tween(animationDuration)) },
        exitTransition = { fadeOut(animationSpec = tween(animationDuration)) },
        popEnterTransition = { fadeIn(animationSpec = tween(animationDuration)) },
        popExitTransition = { fadeOut(animationSpec = tween(animationDuration)) }
    ) {

        composable(Screen.Welcome.route) {
            WelcomeScreen(navController = navController)
        }

        composable(Screen.Library.route) {
            LibraryScreen(navController = navController, app = app)
        }

        composable(Screen.Search.route) {
            SearchScreen(navController = navController, app = app)
        }

        composable(Screen.Recommendations.route) {
            RecommendationsScreen(navController = navController, app = app)
        }

        composable(
            route = Screen.AddOrEdit.route,
            arguments = listOf(
                navArgument("albumId") { type = NavType.StringType },
                navArgument("albumName") { type = NavType.StringType; nullable = true },
                navArgument("artistName") { type = NavType.StringType; nullable = true },
                navArgument("artworkUrl") { type = NavType.StringType; nullable = true },
                navArgument("thumbnailUrl") { type = NavType.StringType; nullable = true },
                navArgument("year") { type = NavType.StringType; nullable = true }
            )
        ) {
            AddScreen(navController = navController, app = app)
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("albumId") { type = NavType.StringType })
        ) {
            DetailScreen(navController = navController, app = app)
        }
    }
}
