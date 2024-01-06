package com.sokamn.trovami.domain.usecase.user

import com.sokamn.trovami.data.network.UserService
import javax.inject.Inject

class GetUserModelByUidUseCase @Inject constructor(private val userService: UserService){
    suspend operator fun invoke(userUID: String) = userService.getUserByUid(userUID)
}