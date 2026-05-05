package com.sokamn.trovami.data.network

import com.sokamn.trovami.domain.model.User
import com.sokamn.trovami.util.AppConstants.Companion.USER_REFERENCE
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserService @Inject constructor(private val firebase: FirebaseClient) {
    suspend fun createUserTable(userSignUp: User) = runCatching {
        userSignUp.uid = firebase.auth.currentUser!!.uid
        firebase.dbRealtime
            .child(USER_REFERENCE)
            .child(userSignUp.uid)
            .setValue(userSignUp).await()
    }.isSuccess

    suspend fun getUser(uid: String): User? {
        var userResponse: User? = User()
        try{
            firebase.dbRealtime
                .child(USER_REFERENCE)
                .child(uid)
                .get()
                .addOnSuccessListener {
                    userResponse = it.getValue(User::class.java)
                }
                .addOnFailureListener {
                    userResponse = null
                }
                .await()
            return userResponse
        }catch (e: Exception){
            return null
        }
    }
}