package com.sokamn.trovami.domain.usecase.auth

import com.sokamn.trovami.data.network.AuthService
import javax.inject.Inject

class LogOutUseCase @Inject constructor(private val authService: AuthService) {
    operator fun invoke() = authService.logOut()
}