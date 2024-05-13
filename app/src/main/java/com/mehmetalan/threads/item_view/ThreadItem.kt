package com.mehmetalan.threads.item_view

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mehmetalan.threads.model.ThreadModel
import com.mehmetalan.threads.model.UserModel
import com.mehmetalan.threads.navigation.Routes
import com.mehmetalan.threads.viewmodel.ThreadItemViewModel
import kotlinx.coroutines.tasks.await

@Composable
fun ThreadItem(
    thread: ThreadModel,
    users: UserModel,
    navController: NavHostController,
    userId: String
) {

    val threadItemViewModel: ThreadItemViewModel = viewModel()

    val formattedTimeStamp = threadItemViewModel.epochToFormattedTime(thread.timeStamp)

    Column {
        ConstraintLayout (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {

                },
        ) {

            val (userImage, userName, date, time, title, image,likeButton,likeNumber) = createRefs()

            Image(
                painter = rememberAsyncImagePainter(model = users.imageUrl),
                contentDescription = "close",
                modifier = Modifier
                    .constrainAs(userImage) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .size(36.dp)
                    .clip(shape = CircleShape)
                    .clickable {
                        val routes = Routes.OtherUsers.routes.replace("{data}", users.uid)
                        navController.navigate(routes)
                    },
                contentScale = ContentScale.Crop
            )

            Text(
                text = "@${users.userName}",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .constrainAs(userName) {
                        top.linkTo(userImage.top)
                        start.linkTo(userImage.end, margin = 12.dp)
                        bottom.linkTo(userImage.bottom)
                    }
                    .clickable {
                        val routes = Routes.OtherUsers.routes.replace("{data}", users.uid)
                        navController.navigate(routes)
                    }
            )

            Text(
                text = thread.thread,
                fontSize = 18.sp,
                modifier = Modifier
                    .constrainAs(title) {
                        top.linkTo(userName.bottom, margin = 8.dp)
                        start.linkTo(userName.start)
                    }
            )
            Text(
                text = formattedTimeStamp,
                modifier = Modifier
                    .constrainAs(likeNumber){
                        top.linkTo(parent.top, margin = 10.dp)
                        end.linkTo(parent.end, margin = 8.dp)
                    }
            )

            if (thread.image != "") {
                Card (
                    modifier = Modifier
                        .constrainAs(image) {
                            top.linkTo(title.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {

                    Image(
                        painter = rememberAsyncImagePainter(model = thread.image),
                        contentDescription = "close",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clickable {
                                val encodedUrl = Uri.encode(thread.image)
                                val route = "fullScreenImage/${encodedUrl}"
                                navController.navigate(route)
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }

        }
        LikeButton(
            threadId = thread.threadId,
            userId = userId,
            modifier = Modifier
                .padding(start = 10.dp, bottom = 10.dp)
        )
        Divider(color = Color.LightGray, thickness = 1.dp)
    }

}

@Composable
fun LikeButton(
    threadId: String,
    userId: String,
    modifier: Modifier = Modifier
) {
    val firestoreDb = FirebaseFirestore.getInstance()
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(0) }
    LaunchedEffect(threadId) {
        val likeDoc = firestoreDb.collection("likeThreads").document(threadId)
        val data = likeDoc.get().await()
        likeCount = data.getLong("likeCount")?.toInt() ?: 0
        isLiked = (data.get("likedBy") as? List<*>)?.contains(userId) ?: false
    }

    Row (
    ) {
        IconButton(
            onClick = {
                // Butonun tıklanma olayını yönet
                isLiked = !isLiked
                if (isLiked) {
                    likeCount++
                    firestoreDb.collection("likeThreads").document(threadId).update(
                        mapOf(
                            "likeCount" to likeCount,
                            "likedBy" to FieldValue.arrayUnion(userId)
                        )
                    )
                } else {
                    likeCount--
                    firestoreDb.collection("likeThreads").document(threadId).update(
                        mapOf(
                            "likeCount" to likeCount,
                            "likedBy" to FieldValue.arrayRemove(userId)
                        )
                    )
                }
            },
            modifier = Modifier
                .padding(start = 10.dp, bottom = 10.dp)
                .size(20.dp),
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite Button",
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = likeCount.toString(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            modifier = Modifier
                .clickable {  }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShowThreadItem() {
   // ThreadItem()
}