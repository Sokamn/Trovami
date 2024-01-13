package com.sokamn.trovami.domain.usecase.user

import com.sokamn.trovami.data.network.UserService
import com.sokamn.trovami.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserEmail @Inject constructor(private val userService: UserService){
    operator fun invoke(): Resource<String> = userService.currentUserEmail
}