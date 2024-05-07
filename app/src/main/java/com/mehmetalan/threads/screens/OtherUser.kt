package com.mehmetalan.threads.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.mehmetalan.threads.item_view.ThreadItem
import com.mehmetalan.threads.model.UserModel
import com.mehmetalan.threads.navigation.Routes
import com.mehmetalan.threads.utils.SharePreferences
import com.mehmetalan.threads.viewmodel.AuthViewModel
import com.mehmetalan.threads.viewmodel.UserViewModel

@Composable
fun OtherUsers(
    navController: NavHostController,
    uid: String
) {
    val authViewModel: AuthViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState(null)

    val context = LocalContext.current

    val userViewModel: UserViewModel = viewModel()
    val threads by userViewModel.threads.observeAsState(null)
    val users by userViewModel.users.observeAsState(null)
    val followerList by userViewModel.followerList.observeAsState(null)
    val followingList by userViewModel.followingList.observeAsState(null)


    userViewModel.fetchThreads(uid)
    userViewModel.fetchUser(uid)
    userViewModel.getFollowers(uid)
    userViewModel.getFollowing(uid)

    var currentUserId = ""

    if (FirebaseAuth.getInstance().currentUser != null) {
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    }

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
                    .padding(16.dp)
            ){

                val(text, logo, userName, bio, followers, followings, button) = createRefs()

                Text(
                    text = users!!.name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .constrainAs(text) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }

                )

                Image(
                    painter = rememberAsyncImagePainter(model = users!!.imageUrl),
                    contentDescription = "close",
                    modifier = Modifier
                        .constrainAs(logo) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }
                        .size(120.dp)
                        .clip(shape = CircleShape)
                        .clickable {
                                   val encodedUrl = Uri.encode(users!!.imageUrl)
                            val route = "fullScreenProfileImage/${encodedUrl}"
                            navController.navigate(route)
                        },
                    contentScale = ContentScale.Crop

                )

                Text(
                    text = users!!.userName,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .constrainAs(userName) {
                            top.linkTo(text.bottom)
                            start.linkTo(parent.start)
                        }
                )

                Text(
                    text = users!!.bio,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .constrainAs(bio) {
                            top.linkTo(userName.bottom)
                            start.linkTo(parent.start)
                        }
                )

                Text(
                    text = "${followerList!!.size} Followers",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .constrainAs(followers) {
                            top.linkTo(bio.bottom)
                            start.linkTo(parent.start)
                        }
                        .clickable {
                            navController.navigate("${Routes.FollowersScreen.routes}/$uid")
                        }
                )

                Text(
                    text = "${followingList!!.size} following",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .constrainAs(followings) {
                            top.linkTo(followers.bottom)
                            start.linkTo(parent.start)
                        }
                        .clickable {
                            navController.navigate("${Routes.FollowingsScreen.routes}/$uid")
                        }
                )

                if (uid != currentUserId) {
                    ElevatedButton(
                        onClick = {
                            if (currentUserId.isNotEmpty()) {
                                if (followerList != null && followerList!!.contains(currentUserId)) {
                                    // Unfollow
                                    userViewModel.unFollowUsers(userId = uid, currentUserId = currentUserId)
                                } else {
                                    // Follow
                                    userViewModel.followUsers(userId = uid, currentUserId = currentUserId)
                                }
                            }
                        },
                        modifier = Modifier
                            .constrainAs(button) {
                                top.linkTo(followings.bottom)
                                start.linkTo(parent.start)
                            }
                    ) {
                        Text(
                            text = if (followerList != null && followerList!!.contains(currentUserId)) {
                                "Following"
                            } else {
                                "Follow"
                            }
                        )
                    }
                }
            }
        }

        if (threads != null && users != null) {
            items(threads ?: emptyList()) {pair ->
                ThreadItem(
                    thread = pair,
                    users = users!!,
                    navController = navController,
                    userId = SharePreferences.getUserName(context))
            }
        }
    }
}