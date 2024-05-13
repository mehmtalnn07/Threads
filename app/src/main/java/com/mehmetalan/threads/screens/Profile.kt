package com.mehmetalan.threads.screens

import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.mehmetalan.threads.R
import com.mehmetalan.threads.item_view.ThreadItem
import com.mehmetalan.threads.model.UserModel
import com.mehmetalan.threads.navigation.Routes
import com.mehmetalan.threads.utils.SharePreferences
import com.mehmetalan.threads.viewmodel.AuthViewModel
import com.mehmetalan.threads.viewmodel.UserViewModel

@Composable
fun Profile(
    navController: NavHostController
) {

    val authViewModel: AuthViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState(null)

    val context = LocalContext.current

    val userViewModel: UserViewModel = viewModel()
    val threads by userViewModel.threads.observeAsState(null)

    val followerList by userViewModel.followerList.observeAsState(null)
    val followingList by userViewModel.followingList.observeAsState(null)

    var currentUserId = ""

    if (FirebaseAuth.getInstance().currentUser != null) {
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    }

    if (currentUserId != "") {
        userViewModel.getFollowers(currentUserId)
        userViewModel.getFollowing(currentUserId)
    }

    val user = UserModel(
        name = SharePreferences.getName(context),
        userName = SharePreferences.getUserName(context),
        imageUrl = SharePreferences.getImage(context)
    )

    if (firebaseUser != null)
        userViewModel.fetchThreads(firebaseUser!!.uid)


     LaunchedEffect(
         firebaseUser
     ) {
         if (firebaseUser == null) {
             navController.navigate(route = Routes.Login.routes) {
                 popUpTo(navController.graph.startDestinationId)
                 launchSingleTop = true
             }
         }
     }

    LazyColumn {
        item {
            ConstraintLayout (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ){

                val(text, logo, userName, bio, followers, followings, button) = createRefs()

                Text(
                    text = SharePreferences.getName(context),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .constrainAs(text) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }

                )

                Image(
                    painter = rememberAsyncImagePainter(model = SharePreferences.getImage(context)),
                    contentDescription = "close",
                    modifier = Modifier
                        .constrainAs(logo) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }
                        .size(120.dp)
                        .clip(shape = CircleShape)
                        .clickable {
                            val encodedUrl = Uri.encode(user.imageUrl)
                            val route = "fullScreenProfileImage/${encodedUrl}"
                            navController.navigate(route)
                        },
                    contentScale = ContentScale.Crop

                )

                Text(
                    text = SharePreferences.getUserName(context),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .constrainAs(userName) {
                            top.linkTo(text.bottom, margin = 5.dp)
                            start.linkTo(parent.start)
                        }
                )

                Text(
                    text = SharePreferences.getBio(context),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .constrainAs(bio) {
                            top.linkTo(userName.bottom, margin = 5.dp)
                            start.linkTo(parent.start)
                        }
                )

                Text(
                    text = "${followerList!!.size} Followers",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .constrainAs(followers) {
                            top.linkTo(bio.bottom, margin = 5.dp)
                            start.linkTo(parent.start)
                        }
                        .clickable {
                            navController.navigate("${Routes.FollowersScreen.routes}/$currentUserId")
                        }
                )

                Text(
                    text = "${followingList!!.size} following",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .constrainAs(followings) {
                            top.linkTo(followers.bottom, margin = 5.dp)
                            start.linkTo(parent.start)
                        }
                        .clickable {
                            navController.navigate("${Routes.FollowingsScreen.routes}/$currentUserId")
                        }
                )

                ElevatedButton(
                    onClick = {
                        authViewModel.logOut()
                    },
                    modifier = Modifier
                        .constrainAs(button) {
                            top.linkTo(followings.bottom, margin = 5.dp)
                            start.linkTo(parent.start)
                        }
                ) {
                    Text(
                        text = stringResource(id = R.string.logout)
                    )
                }
            }
        }

        items(threads ?: emptyList()) {pair ->
            ThreadItem(
                thread = pair,
                users = user,
                navController = navController,
                userId = SharePreferences.getUserName(context)
            )
        }
    }
}