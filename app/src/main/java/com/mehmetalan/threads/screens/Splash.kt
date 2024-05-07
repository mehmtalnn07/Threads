package com.mehmetalan.threads.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.mehmetalan.threads.R
import com.mehmetalan.threads.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun Splash(
    navController: NavHostController
) {

    ConstraintLayout (
        modifier = Modifier
            .fillMaxSize()
    ) {

        val (image) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.twitter),
            contentDescription = "Logo",
            modifier = Modifier
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start )
                    end.linkTo(parent.end)
                }
        )
    }

    LaunchedEffect(true) {
        delay(1000)
        if (FirebaseAuth.getInstance().currentUser != null) {
            navController.navigate(route = Routes.BottomNavigation.routes) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        } else {
            navController.navigate(route = Routes.Login.routes) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }
    }
}