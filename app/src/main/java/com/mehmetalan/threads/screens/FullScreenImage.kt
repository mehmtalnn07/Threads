package com.mehmetalan.threads.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter

@SuppressLint("AutoboxingStateCreation")
@Composable
fun FullScreenImage(
    imageUrl: String,
    navController: NavHostController
) {

    val minScale = 1f
    val maxScale = 4f
    var isZoomedIn by remember { mutableStateOf(false) }
    var rawScale by remember { mutableStateOf(1f) }

    val animatedScale by animateFloatAsState(
        targetValue = if (isZoomedIn) maxScale / 2 else minScale,
        animationSpec = tween(durationMillis = 300)
    )

    val scale = if (rawScale == 1f) animatedScale else rawScale
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        val newScale = rawScale * zoom
                        if (newScale in minScale..maxScale) {
                            rawScale = newScale
                            isZoomedIn = rawScale > minScale
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            isZoomedIn = !isZoomedIn
                            if (!isZoomedIn) {
                                rawScale = 1f
                            }
                        }
                    )
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "Zoomable image",
                contentScale = ContentScale.Fit
            )

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
    }
}