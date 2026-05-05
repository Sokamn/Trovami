package com.sokamn.trovami.domain.usecase

import com.sokamn.trovami.data.network.AuthenticationService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerifyEmailUseCase @Inject constructor(private val authenticationService: AuthenticationService) {
    operator fun invoke(): Flow<Boolean> = authenticationService.verifiedAccount
    fun emailVerified(): Flow<String> = authenticationService.emailVerified
    fun existUserConnected(): Flow<Boolean> = authenticationService.existUserConnected
}