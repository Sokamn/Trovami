package com.sokamn.trovami.domain.usecase.auth

import com.sokamn.trovami.data.network.AuthService
import com.sokamn.trovami.utils.Resource
import javax.inject.Inject

class EmailLoginUseCase @Inject constructor(private val authenticationService: AuthService) {
    suspend operator fun invoke(email: String, password: String) = authenticationService.login(email, password)
}