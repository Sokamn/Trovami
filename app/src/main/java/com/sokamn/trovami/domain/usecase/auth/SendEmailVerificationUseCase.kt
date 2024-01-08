package com.sokamn.trovami.domain.usecase.auth

import com.sokamn.trovami.data.network.AuthService
import javax.inject.Inject

class SendEmailVerificationUseCase @Inject constructor(private val authService: AuthService) {
    suspend operator fun invoke() = authService.sendVerificationEmail()
}