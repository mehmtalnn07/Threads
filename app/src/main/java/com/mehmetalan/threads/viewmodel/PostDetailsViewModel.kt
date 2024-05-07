package com.mehmetalan.threads.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mehmetalan.threads.model.ThreadModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PostDetailsViewModel : ViewModel() {
    private val databaseReference = FirebaseDatabase.getInstance().getReference("threads") // Firebase referansı

    fun getThreadDetails(threadId: String): Flow<ThreadModel?> {
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val thread = dataSnapshot.child(threadId).getValue(ThreadModel::class.java) // Veri modeliyle eşleşen veri
                    trySend(thread) // emit yerine `trySend` kullanılıyor
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    trySend(null) // Hata durumunda null gönder
                }
            }

            databaseReference.addValueEventListener(listener) // Veri dinleyicisi ekle

            awaitClose { // Akış kapandığında dinleyiciyi kaldır
                databaseReference.removeEventListener(listener)
            }
        }
    }
}