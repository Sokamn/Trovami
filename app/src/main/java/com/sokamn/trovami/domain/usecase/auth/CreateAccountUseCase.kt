package com.sokamn.trovami.domain.usecase.auth

import com.sokamn.trovami.data.network.AuthService
import com.sokamn.trovami.data.network.UserService
import com.sokamn.trovami.domain.model.UserModel
import com.sokamn.trovami.domain.model.UserResponse
import com.sokamn.trovami.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val authService: AuthService,
    private val userService: UserService
) {
    suspend operator fun invoke(userSignIn: UserModel): Resource<UserResponse>{
        val response = authService.createAccount(userSignIn.email, userSignIn.password)
        return if (response is Resource.Success){
            userSignIn.uid = response.data.userUID

            when(userService.createUserTable(userSignIn)){ // SI SALE MAL BORRAR AUTH
                is Resource.Success -> response
                is Resource.Error -> Resource.Error("DB ERROR")
            }
        }else{
            response
        }
    }
}