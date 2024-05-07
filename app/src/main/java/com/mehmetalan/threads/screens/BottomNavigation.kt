package com.mehmetalan.threads.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mehmetalan.threads.model.BottomNavigationItem
import com.mehmetalan.threads.navigation.Routes

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BottomNav(
    navController: NavHostController
) {
    val navController1 = rememberNavController()

    Scaffold (
        bottomBar = {
            MyBottomBar(navController1)
        }
    ) {innerPadding ->
        NavHost(
            navController = navController1,
            startDestination = Routes.Home.routes,
            modifier = Modifier
                .padding(innerPadding)
        ) {
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
                    navController = navController1
                )
            }
            composable(route = Routes.Profile.routes) {
                Profile(
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun MyBottomBar(
    navController1: NavHostController
){

    val backStackEntry = navController1.currentBackStackEntryAsState()

    val list = listOf(
        BottomNavigationItem(
            title = "Home",
            route = Routes.Home.routes,
            selectedIcon = Icons.Filled.Home,
            unSelectedIcon = Icons.Outlined.Home
        ),
        BottomNavigationItem(
            title = "Search",
            route = Routes.Search.routes,
            selectedIcon = Icons.Filled.Search,
            unSelectedIcon = Icons.Outlined.Search
        ),
        BottomNavigationItem(
            title = "Add Threads",
            route = Routes.AddThreads.routes,
            selectedIcon = Icons.Filled.Add,
            unSelectedIcon = Icons.Outlined.Add
        ),
        BottomNavigationItem(
            title = "Notification",
            route = Routes.Notification.routes,
            selectedIcon = Icons.Filled.Notifications,
            unSelectedIcon = Icons.Outlined.Notifications
        ),
        BottomNavigationItem(
            title = "Profile",
            route = Routes.Profile.routes,
            selectedIcon = Icons.Filled.Person,
            unSelectedIcon = Icons.Outlined.Person
        )
    )

    BottomAppBar {
        list.forEach {

            val selected: Boolean = it.route == backStackEntry?.value?.destination?.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                          navController1.navigate(it.route) {
                              popUpTo(navController1.graph.findStartDestination().id) {
                                  saveState = true
                              }
                              launchSingleTop = true
                          }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) it.selectedIcon else it.unSelectedIcon,
                        contentDescription = it.title
                    )
                }
            )

        }
    }

}