package com.sokamn.trovami.domain.usecase.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.sokamn.trovami.data.network.AuthService
import javax.inject.Inject

class GoogleLoginUseCase @Inject constructor(private val authenticationService: AuthService) {
    suspend operator fun invoke(account: GoogleSignInAccount) = authenticationService.loginGoogle(account)
}