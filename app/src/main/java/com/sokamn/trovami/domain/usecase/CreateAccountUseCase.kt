package com.sokamn.trovami.domain.usecase

import com.sokamn.trovami.data.network.AuthenticationService
import com.sokamn.trovami.data.network.UserService
import com.sokamn.trovami.domain.model.User
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: UserService
) {

    suspend operator fun invoke(userSignIn: User): Boolean {
        val accountCreated =
            authenticationService.createAccount(userSignIn.email, userSignIn.password) != null
        return if (accountCreated) {
            userService.createUserTable(userSignIn)
        } else {
            false
        }
    }
}