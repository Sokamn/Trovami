package com.sokamn.trovami.domain.usecase

import com.sokamn.trovami.data.network.AuthenticationService
import com.sokamn.trovami.data.response.LoginResponse
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authenticationService: AuthenticationService) {
    suspend operator fun invoke(email: String, password: String): LoginResponse =
        authenticationService.login(email, password)
}