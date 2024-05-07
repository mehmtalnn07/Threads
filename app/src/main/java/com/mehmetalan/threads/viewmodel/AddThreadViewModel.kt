package com.mehmetalan.threads.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mehmetalan.threads.model.ThreadModel
import com.mehmetalan.threads.model.UserModel
import com.mehmetalan.threads.utils.SharePreferences
import java.util.UUID

class AddThreadViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance()
    val userReference = db.getReference("threads")

    private val storeageReference = Firebase.storage.reference
    private val imageReference = storeageReference.child("threads/${UUID.randomUUID()}.jpg")

    private val _isPosted = MutableLiveData<Boolean>()
    val isPosted: LiveData<Boolean> = _isPosted


    fun saveImage(thread: String, userId: String, imageUri: Uri) {

        val uploadTask = imageReference.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            imageReference.downloadUrl.addOnSuccessListener {
                saveData(thread, userId, it.toString())
            }
        }

    }

    fun saveData(
        thread: String,
        userId: String,
        image: String,
    ) {

        val firestoreDb = FirebaseFirestore.getInstance()
        val threadId = userReference.push().key ?: UUID.randomUUID().toString()
        val threadData = ThreadModel(
            thread = thread,
            image = image,
            userId = userId,
            timeStamp = System.currentTimeMillis().toString(),
            threadId = threadId
        )


        userReference.child(threadId).setValue(threadData)
            .addOnSuccessListener {
                _isPosted.postValue(true)

                // Firestore'da "likeThreads" koleksiyonunda başlık için başlangıç belgesi oluşturun
                val likeData = hashMapOf(
                    "likeCount" to 0,  // Başlangıç beğeni sayısı
                    "likedBy" to emptyList<String>()  // Başlangıç beğenen kullanıcılar listesi
                )

                firestoreDb.collection("likeThreads").document(threadId).set(likeData, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("AddThreadViewModel", "likeThreads koleksiyonuna belge eklendi")
                    }
                    .addOnFailureListener { e ->
                        Log.e("AddThreadViewModel", "likeThreads belgesi eklenemedi: ", e)
                    }

            }.addOnFailureListener {
                _isPosted.postValue(false)
            }

    }

}