package com.sokamn.trovami.data.network

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sokamn.trovami.data.network.FirebaseConstants.FULLNAME_REFERENCE
import com.sokamn.trovami.data.network.FirebaseConstants.PROFILE_PICTURE_JPG
import com.sokamn.trovami.data.network.FirebaseConstants.USER_REFERENCE
import com.sokamn.trovami.domain.model.UserModel
import com.sokamn.trovami.utils.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserService @Inject constructor(private val firebase: FirebaseClient) {
    fun logOut(){
        try{
            firebase.auth.signOut()
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.toString())
        }
    }

    suspend fun getUserName(uid: String): Resource<String>{
        var userNameResponse: String? = null
        try{
            firebase.dbRealtime
                .child(USER_REFERENCE)
                .child(uid)
                .child(FULLNAME_REFERENCE)
                .get()
                .addOnSuccessListener {
                    userNameResponse = it.value.toString()
                }
                .addOnFailureListener {
                    userNameResponse = null
                }
                .await()
            return if(userNameResponse != null){
                Resource.Success(userNameResponse!!)
            }else{
                Resource.Error("null")
            }
        }catch (e: Exception){
            return Resource.Error(e.toString())
        }
    }

    suspend fun createUserTable(userSignUp: UserModel) : Resource<Unit> {
        return try {
            var isSuccesful = true
            firebase.dbRealtime
                .child(USER_REFERENCE)
                .child(userSignUp.uid)
                .setValue(userSignUp)
                .addOnCompleteListener {
                    isSuccesful = it.isSuccessful
                }
                .await()

            if (isSuccesful){
                Resource.Success(Unit)
            }else{
                Resource.Error("Network Error")
            }
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    suspend fun getUser(uid: String,currentUser: Boolean): Resource<UserModel> {
        firebase.currentUser?.reload()?.await()
        var userResponse: UserModel? = null
        try{
            if (currentUser){
                firebase.dbRealtime
                    .child(USER_REFERENCE)
                    .child(firebase.currentUser!!.uid)
                    .get()
                    .addOnCompleteListener {
                        userResponse = if (it.isSuccessful){
                            it.result.getValue(UserModel::class.java)
                        }else{
                            null
                        }
                    }.await()
            }else{
                firebase.dbRealtime
                    .child(USER_REFERENCE)
                    .child(uid)
                    .get()
                    .addOnCompleteListener {
                        userResponse = if (it.isSuccessful){
                            it.result.getValue(UserModel::class.java)
                        }else{
                            null
                        }
                    }.await()
            }
            return if (userResponse != null){
                Resource.Success(userResponse!!)
            }else{
                Resource.Error("null")
            }
        }catch (e: Exception){
            return Resource.Error(e.toString())
        }
    }

    suspend fun saveProfilePicture(profilePicture: Uri) : Resource<Unit> {
        return try{
            var isSuccessful = false
            firebase.dbStorage
                .child("$USER_REFERENCE/${Firebase.auth.currentUser!!.uid}/$PROFILE_PICTURE_JPG")
                .putFile(profilePicture)
                .addOnCompleteListener{ isSuccessful = it.isSuccessful}.await()
            if (isSuccessful){
                Resource.Success(Unit)
            }else{
                Resource.Error("Network Error")
            }
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }

    }

    suspend fun getProfilePicture(uid: String, currentUser: Boolean): Resource<Uri>{
        var profilePictureResponse: Uri? = null
        try{
            if (currentUser){
                firebase.dbStorage
                    .child("$USER_REFERENCE/${Firebase.auth.currentUser!!.uid}/$PROFILE_PICTURE_JPG")
                    .downloadUrl
                    .addOnCompleteListener {
                        profilePictureResponse = if (it.isSuccessful){
                            it.result
                        }else{
                            null
                        }
                    }.await()
            }else{
                firebase.dbStorage
                    .child("$USER_REFERENCE/$uid/$PROFILE_PICTURE_JPG")
                    .downloadUrl
                    .addOnCompleteListener {
                        profilePictureResponse = if (it.isSuccessful){
                            it.result
                        }else{
                            null
                        }
                    }.await()
            }
            return if(profilePictureResponse != null){
                Resource.Success(profilePictureResponse!!)
            }else{
                Resource.Error("null")
            }

        }catch (e: Exception){
            return Resource.Error(e.toString())
        }
    }
}