package com.sokamn.trovami.presentation.signup

data class SignInViewState(
    val isLoading: Boolean = false,
    val isValidEmail: Boolean = true,
    val isValidPassword: Boolean = true,
    val isValidFullName: Boolean = true,
    val isValidDocument: Boolean = true,
    val isValidProvince: Boolean = true,
    val isValidMunicipality: Boolean = true,
    val isValidAddress: Boolean = true,
    val isValidPhone: Boolean = true,

    ){
    fun userValidated() = isValidEmail && isValidFullName && isValidDocument && isValidProvince && isValidMunicipality && isValidAddress && isValidPhone && isValidPassword
}