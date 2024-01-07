package com.sokamn.trovami.domain.usecase.auth

import com.sokamn.trovami.data.network.AuthService
import com.sokamn.trovami.utils.Resource
import javax.inject.Inject

class HasBeenEmailUsedUseCase @Inject constructor(private val authService: AuthService) {
    suspend operator fun invoke(email: String): Resource<Boolean> = authService.emailExist(email)
}