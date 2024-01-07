package com.sokamn.trovami.domain.usecase.auth

import com.sokamn.trovami.data.network.UserService
import com.sokamn.trovami.domain.model.UserModel
import com.sokamn.trovami.utils.Resource
import javax.inject.Inject

class CreateUserTableUseCase @Inject constructor(private val userService: UserService){
    suspend operator fun invoke(userSignIn: UserModel): Resource<String> = userService.createUserTable(userSignIn)
}