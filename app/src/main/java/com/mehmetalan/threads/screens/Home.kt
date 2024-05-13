package com.mehmetalan.threads.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.mehmetalan.threads.item_view.ThreadItem
import com.mehmetalan.threads.viewmodel.HomeViewModel

@Composable
fun Home(
    navController: NavHostController
) {

    val homeViewModel: HomeViewModel = viewModel()
    val threadAndUsers by homeViewModel.threadsAndUsers.observeAsState(null)

    LazyColumn{
        items(threadAndUsers ?: emptyList()) {pairs ->
            ThreadItem(thread = pairs.first, users = pairs.second, navController, FirebaseAuth.getInstance().currentUser!!.uid)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowHome() {
    //Home()
}