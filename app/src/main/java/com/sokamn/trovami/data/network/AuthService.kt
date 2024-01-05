package com.sokamn.trovami.data.network

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.sokamn.trovami.domain.model.UserResponse
import com.sokamn.trovami.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(private val firebaseClient: FirebaseClient){
    val verifiedAccount: Flow<Boolean> = flow {
        while (true) {
            val verified = verifyEmailIsVerified()
            emit(verified)
            delay(1000)
        }
    }

    val currentUserUID: Flow<String> = flow {
        do {
            val userUID = firebaseClient.currentUser?.uid
            if (userUID != null) {
                emit(userUID)
            }
            delay(1000)
        } while (firebaseClient.currentUser?.uid == null)
    }

    val emailVerified: Flow<String> = flow {
        do {
            val email = firebaseClient.currentUser?.email
            if (email != null) {
                emit(email)
            }
            delay(1000)
        } while (firebaseClient.currentUser?.email == null)
    }

    val userConnectedExist: Flow<Boolean> = flow {
        do {
            emit(firebaseClient.currentUser != null)
            delay(1000)
        } while (firebaseClient.currentUser == null)
    }

    suspend fun emailExist(email: String): Resource<Boolean> {
        return try {
            var exists = false
            firebaseClient.auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
                exists = if (task.isSuccessful) {
                    !task.result.signInMethods.isNullOrEmpty()
                } else {
                    false
                }
            }.await()
            Resource.Success(exists)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    suspend fun sendPasswordRecovery(email: String): Resource<Boolean> {
        return try {
            var isSuccessful = false
            firebaseClient.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { isSuccessful = it.isSuccessful }
                .await()
            Resource.Success(isSuccessful)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }

    }

    suspend fun loginGoogle(account: GoogleSignInAccount): Resource<Unit> = runCatching {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseClient.auth.signInWithCredential(credential).await()
    }.toResourceResponse()

    suspend fun login(email: String, password: String): Resource<Unit> = runCatching {
        firebaseClient.auth.signInWithEmailAndPassword(email, password).await()
    }.toResourceResponse()

    suspend fun createAccount(email: String, password: String): Resource<Unit> = kotlin.runCatching {
        firebaseClient.auth.createUserWithEmailAndPassword(email, password).await()
    }.toResourceResponse()

    suspend fun sendVerificationEmail() = runCatching {
        firebaseClient.currentUser?.sendEmailVerification()?.await() ?: false
    }.isSuccess


    private suspend fun verifyEmailIsVerified(): Boolean {
        firebaseClient.currentUser?.reload()?.await()
        return firebaseClient.currentUser?.isEmailVerified ?: false
    }

    private fun Result<AuthResult>.toResourceResponse() = when (val result = getOrNull()) {
        null -> Resource.Error("Null Pointer Exception")
        else -> {
            Resource.Loading
            val userId = result.user
            checkNotNull(userId)
            Resource.Success(
                UserResponse(
                    result.user?.isEmailVerified ?: false,
                    result.user?.uid ?: "ERROR"
                )
            )
            Resource.Finished
        }
    }

}