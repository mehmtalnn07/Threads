package com.mehmetalan.threads.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.android.play.integrity.internal.al
import com.mehmetalan.threads.screens.AddThreads
import com.mehmetalan.threads.screens.BottomNav
import com.mehmetalan.threads.screens.FollowersScreen
import com.mehmetalan.threads.screens.FollowingsScreen
import com.mehmetalan.threads.screens.FullScreenImage
import com.mehmetalan.threads.screens.FullScreenProfileImage
import com.mehmetalan.threads.screens.Home
import com.mehmetalan.threads.screens.Login
import com.mehmetalan.threads.screens.Notification
import com.mehmetalan.threads.screens.OtherUsers
import com.mehmetalan.threads.screens.Profile
import com.mehmetalan.threads.screens.Register
import com.mehmetalan.threads.screens.Search
import com.mehmetalan.threads.screens.Splash

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash.routes
    ) {
        composable(route = Routes.Splash.routes) {
            Splash(
                navController = navController
            )
        }
        composable(route = Routes.Home.routes) {
            Home(
                navController = navController
            )
        }
        composable(route = Routes.Notification.routes) {
            Notification()
        }
        composable(route = Routes.Search.routes) {
            Search(
                navController = navController
            )
        }
        composable(route = Routes.AddThreads.routes) {
            AddThreads(
                navController = navController
            )
        }
        composable(route = Routes.Profile.routes) {
            Profile(
                navController = navController
            )
        }
        composable(route = Routes.Splash.routes) {
            Splash(
                navController = navController
            )
        }
        composable(route = Routes.BottomNavigation.routes) {
            BottomNav(
                navController = navController
            )
        }
        composable(route = Routes.Login.routes) {
            Login(
                navController = navController
            )
        }
        composable(route = Routes.Register.routes) {
            Register(
                navController = navController
            )
        }
        composable(route = Routes.OtherUsers.routes) {
            val data = it.arguments!!.getString("data")
            OtherUsers(
                navController = navController,
                data!!
            )
        }
        composable(route = "${Routes.FollowersScreen.routes}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            val userId = it.arguments?.getString("userId")
            FollowersScreen(userId = userId.orEmpty(), navController)
        }
        composable(route = "${Routes.FollowingsScreen.routes}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType})
        ) {
            val userId = it.arguments?.getString("userId")
            FollowingsScreen(userId = userId.orEmpty(), navController = navController)
        }
        composable(
            route = "fullScreenImage/{imageUrl}",
            arguments = listOf(navArgument("imageUrl") { type = NavType.StringType })
        ) {
            val imageUrl = it.arguments?.getString("imageUrl") ?: ""
            FullScreenImage(imageUrl, navController)
        }
        composable(
            route = "fullScreenProfileImage/{imageUrl}",
            arguments = listOf(navArgument("imageUrl"){ type = NavType.StringType })
        ) {
            val imageUrl = it.arguments?.getString("imageUrl") ?: ""
            FullScreenProfileImage(imageUrl = imageUrl, navController = navController)
        }
    }
}