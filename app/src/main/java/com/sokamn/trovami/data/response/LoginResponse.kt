package com.sokamn.trovami.data.response

sealed class LoginResponse {
    object Error : LoginResponse()
    data class Success(val verified: Boolean) : LoginResponse()
}