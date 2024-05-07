package com.mehmetalan.threads.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mehmetalan.threads.model.ThreadModel
import com.mehmetalan.threads.model.UserModel
import java.util.UUID

class UserViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance()
    val threadReference = db.getReference("threads")
    val userReference = db.getReference("users")

    private val _threads = MutableLiveData(listOf<ThreadModel>())
    val threads : LiveData<List<ThreadModel>> get() = _threads

    private val _followerList = MutableLiveData(listOf<String>())
    val followerList : LiveData<List<String>> get() = _followerList

    private val _followingList = MutableLiveData(listOf<String>())
    val followingList : LiveData<List<String>> get() = _followingList

    private val _users = MutableLiveData(UserModel())
    val users : LiveData<UserModel> get() = _users

    private val _userDetailsList = MutableLiveData<List<UserModel>>()
    val userDetailsList: MutableLiveData<List<UserModel>> = _userDetailsList

    fun fetchUser(uid: String) {

        userReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                _users.postValue(user)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    fun fetchThreads(uid: String) {

        threadReference.orderByChild("userId").equalTo(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val threadList = snapshot.children.mapNotNull {
                    it.getValue(ThreadModel::class.java)
                }
                _threads.postValue(threadList)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    val firestoreDb = Firebase.firestore

    fun followUsers(userId: String, currentUserId: String) {
        val ref = firestoreDb.collection("following").document(currentUserId)
        val followerReference = firestoreDb.collection("followers").document(userId)

        ref.update("followingIds", FieldValue.arrayUnion(userId))
        followerReference.update("followerIds", FieldValue.arrayUnion(currentUserId))

    }

    fun unFollowUsers(userId: String, currentUserId: String) {
        val followingReference = firestoreDb.collection("following").document(currentUserId)
        val followerReference = firestoreDb.collection("followers").document(userId)

        followingReference.update("followingIds", FieldValue.arrayRemove(userId))
        followerReference.update("followerIds", FieldValue.arrayRemove(currentUserId))
    }

    fun getFollowers(userId: String) {
        firestoreDb.collection("followers").document(userId)
            .addSnapshotListener { value, error ->
                val followerIds = value?.get("followerIds") as? List<String>?: listOf()
                _followerList.postValue(followerIds)
                if (followerIds.isNotEmpty()) {  // Eğer takipçi kimlikleri varsa
                    fetchUserDetails(followerIds)  // Kullanıcı detaylarını al
                }
            }
    }

    fun getFollowing(userId: String) {
        firestoreDb.collection("following").document(userId)
            .addSnapshotListener { value, error ->
                val followingIds = value?.get("followingIds") as? List<String> ?: listOf()
                _followingList.postValue(followingIds)
                if (followingIds.isNotEmpty()) {
                    fetchUserDetails(followingIds)
                }
            }
    }

    val realtimeDb = FirebaseDatabase.getInstance()

    fun fetchUserDetails(userIds: List<String>) {
        val userDetails = mutableListOf<UserModel>()
        for (id in userIds) {
            realtimeDb.getReference("users").child(id)  // "users" koleksiyonundan belirli bir kullanıcıyı al
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(UserModel::class.java)
                        if (user != null) {
                            userDetails.add(user)
                        }
                        if (userDetails.size == userIds.size) {
                            _userDetailsList.postValue(userDetails)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("RealtimeDatabaseError", "Error fetching user details: ${databaseError.message}")
                    }
                })
        }
    }

}