package com.sokamn.trovami.domain.model

data class User (
    var uid: String = "",
    var fullName: String = "",
    var biography: String = "",
    var document: String = "",
    var phoneNumber: String = "",
    var password: String = "",
    var email: String = "",
    var defaultAdress: String = "",
    var province: String = "",
    var municipality: String = "",
    var masterList: MutableList<Master> = mutableListOf()
) {
    fun isNotEmpty() =
        fullName.isNotEmpty() && document.isNotEmpty() && phoneNumber.isNotEmpty() &&
                password.isNotEmpty() && email.isNotEmpty() && defaultAdress.isNotEmpty() && province.isNotEmpty() && municipality.isNotEmpty()

}