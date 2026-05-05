package com.sokamn.trovami.domain.usecase

import com.sokamn.trovami.data.network.AuthenticationService
import javax.inject.Inject

class PasswordRecoveryUseCase @Inject constructor(private val authenticationService: AuthenticationService) {
    suspend operator fun invoke(email: String) = authenticationService.sendPasswordRecovery(email)
}