package com.mehmetalan.threads.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.mehmetalan.threads.screens.FullScreenImage

sealed class Routes(
    val routes: String
) {

    object Home: Routes(routes = "home")
    object Notification: Routes(routes = "notification")
    object Profile: Routes(routes = "profile")
    object Search: Routes(routes = "search")
    object Splash: Routes(routes = "splash")
    object AddThreads: Routes(routes = "addThreads")
    object BottomNavigation: Routes(routes = "bottomNavigation")
    object Login: Routes(routes = "login")
    object Register: Routes(routes = "register")
    object OtherUsers: Routes(routes = "otherUsers/{data}")
    object FollowersScreen: Routes(routes = "followersScreen")
    object FollowingsScreen: Routes(routes = "followingsScreen")
    object ThreadsDetailsScreen: Routes(routes = "")
}