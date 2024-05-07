package com.mehmetalan.threads.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.mehmetalan.threads.model.UserModel
import com.mehmetalan.threads.navigation.Routes
import com.mehmetalan.threads.viewmodel.UserViewModel

@Composable
fun FollowersScreen(
    userId: String,
    navController: NavHostController
    ) {
    val userViewModel: UserViewModel = viewModel() // ViewModel örneğini alın

    // ViewModel'den takipçi kimliklerini almak için çağır
    LaunchedEffect(userId) {
        userViewModel.getFollowers(userId) // Takipçileri alır
    }

    // UserDetailsList'i gözlemle
    val userDetailsList by userViewModel.userDetailsList.observeAsState(emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(userDetailsList) { user ->
            UserCard(
                user,
                navController
            )
        }
    }
}

@Composable
private fun UserCard(
    user: UserModel,
    navController: NavHostController,
) {

    val userViewModel: UserViewModel = viewModel()
    val followerList by userViewModel.followerList.observeAsState(null)
    val followingList by userViewModel.followingList.observeAsState(null)

    var followerId: String? = null

    for (followerUserId in followerList!!) {
        followerId = followerUserId
    }

    var currentUserId = ""

    if (FirebaseAuth.getInstance().currentUser != null) {
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val routes = Routes.OtherUsers.routes.replace("{data}", user.uid)
                navController.navigate(routes)
            },
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Image(
                    painter = rememberImagePainter(data = user.imageUrl),
                    contentDescription = "${user.name}'s profile picture",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),  // Yuvarlak profil resmi
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Kullanıcı adı ve kullanıcı adı
                Column {
                    Text(text = user.name, fontSize = 18.sp)
                    Text(text = "@${user.userName}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}