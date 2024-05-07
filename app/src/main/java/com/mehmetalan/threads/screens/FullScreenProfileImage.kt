package com.mehmetalan.threads.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

@Composable
fun FullScreenProfileImage(
    imageUrl: String,
    navController: NavHostController
) {

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid // Eğer kullanıcı oturum açmışsa `uid`

    // Eğer kullanıcı oturum açmamışsa uygun bir işlem yapın
    if (userId == null) {
        Text("Kullanıcı oturum açmamış!")
        return
    }

    var isDialogOpen by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Firebase referansları
    val storage = FirebaseStorage.getInstance() // Firebase Storage referansı
    val database = FirebaseDatabase.getInstance() // Firebase Realtime Database referansı
    val userDatabaseRef = database.getReference("users/$userId") // İlgili kullanıcıya özel referans

    // Galeriden fotoğraf seçmek için ActivityResultLauncher
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri

            // Firebase Storage'a fotoğraf yükleme
            val storageRef = storage.reference.child("users/${userId}/${uri.lastPathSegment}")
            storageRef.putFile(uri).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // İndirme URL'sini al
                    storageRef.downloadUrl.addOnCompleteListener { downloadTask ->
                        if (downloadTask.isSuccessful) {
                            val downloadUrl = downloadTask.result
                            // Realtime Database'deki `imageUrl`'i güncelle
                            userDatabaseRef.child("imageUrl").setValue(downloadUrl.toString())
                        }
                    }
                }
            }
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = selectedImageUri ?: imageUrl // Seçilen resim veya mevcut resim
                ),
                contentDescription = "Full screen image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(shape = CircleShape)
                    .clickable {
                        navController.popBackStack()
                    },
                contentScale = ContentScale.Crop
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

            OutlinedButton(
                onClick = {
                    isDialogOpen = true
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = "Düzenle"
                )
            }
            if (isDialogOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize() // Dialog'un tam genişliğe yayılması için
                        .background(Color(0x80000000)) // Yarı saydam arka plan için
                        .clickable { isDialogOpen = false }, // Ekrana tıklayınca kapat
                    contentAlignment = Alignment.Center // İçeriği ortala
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth() // Tam genişlik
                            .padding(24.dp) // Kenarlardan boşluk
                            .clip(RoundedCornerShape(16.dp)) // Yuvarlatılmış köşeler
                            .background(Color.White), // Beyaz arka plan
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Text(
                                "Fotoğraf Çek",
                                modifier = Modifier
                                    .clickable {
                                        // Fotoğraf çekme işlemi
                                        isDialogOpen = false
                                    }
                                    .padding(16.dp) // Boşluklar
                            )
                            Divider() // Bölücü çizgi
                            Text(
                                "Mevcut Fotoğraflardan Seç",
                                modifier = Modifier
                                    .clickable {
                                        // Mevcut fotoğrafları seçmek için
                                        galleryLauncher.launch("image/*")
                                        isDialogOpen = false
                                    }
                                    .padding(16.dp) // Boşluklar
                            )
                        }
                    }
                }
            }
        }
    }
}