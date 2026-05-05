package com.sokamn.trovami.domain.usecase

import com.sokamn.trovami.data.network.AuthenticationService
import javax.inject.Inject

class SendEmailVerificationUseCase @Inject constructor(private val authenticationService: AuthenticationService) {
    suspend operator fun invoke() = authenticationService.sendVerificationEmail()
}