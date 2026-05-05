package com.sokamn.trovami.data.network

import com.google.firebase.auth.AuthResult
import com.sokamn.trovami.data.response.LoginResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationService @Inject constructor(private val firebase: FirebaseClient) {

    val verifiedAccount: Flow<Boolean> = flow {
        while (true) {
            val verified = verifyEmailIsVerified()
            emit(verified)
            delay(1000)
        }
    }

    val emailVerified: Flow<String> = flow {
        firebase.auth.currentUser?.email?.let { emit(it) }
    }

    val existUserConnected: Flow<Boolean> = flow {
        emit(firebase.auth.currentUser!=null)
    }

    suspend fun sendPasswordRecovery(email: String): Flow<Boolean> = flow {
        try {
            var isSuccessful = false
            firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { isSuccessful = it.isSuccessful }
                .await()
            emit(isSuccessful)
        }catch (e: Exception){
            emit(false)
        }

    }

    suspend fun login(email: String, password: String): LoginResponse = runCatching {
        firebase.auth.signInWithEmailAndPassword(email, password).await()
    }.toLoginResponse()

    suspend fun createAccount(email: String, password: String): AuthResult? {
        return firebase.auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun sendVerificationEmail() = runCatching {
        firebase.auth.currentUser?.sendEmailVerification()?.await() ?: false
    }.isSuccess

    private suspend fun verifyEmailIsVerified(): Boolean {
        firebase.auth.currentUser?.reload()?.await()
        return firebase.auth.currentUser?.isEmailVerified ?: false
    }

    private fun Result<AuthResult>.toLoginResponse() = when (val result = getOrNull()) {
        null -> LoginResponse.Error
        else -> {
            val userId = result.user
            checkNotNull(userId)
            LoginResponse.Success(result.user?.isEmailVerified ?: false)
        }
    }


}