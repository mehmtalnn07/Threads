package com.mehmetalan.threads.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mehmetalan.threads.navigation.Routes
import com.mehmetalan.threads.viewmodel.AuthViewModel

@Composable
fun Login(
    navController: NavHostController
) {

    val authViewModel: AuthViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState()
    val error by authViewModel.error.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(
        firebaseUser
    ) {
        if (firebaseUser != null) {
            navController.navigate(route = Routes.BottomNavigation.routes) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }
    }



    error?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Login",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp
        )

        Box(
            modifier = Modifier
                .height(50.dp)
        )
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Text(
                    text = "Email"
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .height(30.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    text = "Password"
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .height(30.dp)
        )

        OutlinedButton(
            onClick = {
                if (email.isEmpty()||password.isEmpty()) {
                    Toast.makeText(context,"Please provide all fields",Toast.LENGTH_SHORT).show()
                } else {
                    authViewModel.login(email, password, context)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Login",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(vertical = 6.dp)
            )
        }

        TextButton(
            onClick = {
                navController.navigate(route = Routes.Register.routes) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        ) {
            Text(
                text = "New User? Create Account",
                fontSize = 16.sp
            )
        }

    }

}

@Preview(showBackground = true)
@Composable
fun LoginView() {
    Login(
        navController = rememberNavController()
    )
}