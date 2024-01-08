package com.sokamn.trovami.domain.usecase.auth

import com.sokamn.trovami.data.network.AuthService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsEmailVerifiedUseCase @Inject constructor(private val authService: AuthService) {
    operator fun invoke(): Flow<Boolean> = authService.verifiedAccount
}