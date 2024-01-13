package com.sokamn.trovami.domain.usecase.auth

import com.sokamn.trovami.data.network.AuthService
import javax.inject.Inject

class RecoverPasswordUseCase @Inject constructor(private val authService: AuthService) {
    suspend operator fun invoke(email: String) = authService.sendPasswordRecovery(email)
}