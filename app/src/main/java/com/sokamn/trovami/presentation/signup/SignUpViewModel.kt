package com.sokamn.trovami.presentation.signup

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokamn.trovami.core.Event
import com.sokamn.trovami.domain.model.User
import com.sokamn.trovami.domain.usecase.CreateAccountUseCase
import com.sokamn.trovami.util.AppConstants.Companion.BAD_WORDS
import com.sokamn.trovami.util.AppConstants.Companion.MIN_SIGNUP_LENGTH
import com.sokamn.trovami.util.AppConstants.Companion.PASSWORD_REGEX
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(val createAccountUseCase: CreateAccountUseCase) :
    ViewModel() {

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin

    private val _navigateToVerifyEmail = MutableLiveData<Event<Boolean>>()
    val navigateToVerifyEmail: LiveData<Event<Boolean>>
        get() = _navigateToVerifyEmail

    private val _viewState = MutableStateFlow(SignInViewState())
    val viewState: StateFlow<SignInViewState>
        get() = _viewState

    private var _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean>
        get() = _showErrorDialog

    private var _showErrorInputs = MutableLiveData(false)
    val showErrorInputs: LiveData<Boolean>
        get() = _showErrorInputs

    fun onSignUpSelected(userSignIn: User, passwordConfirmation: String?) {
        val viewState = userSignIn.toSignInViewState(passwordConfirmation)
        if (viewState.userValidated() && userSignIn.isNotEmpty()) {
            signUpUser(userSignIn)
        } else {
            onFieldsChanged(userSignIn, passwordConfirmation)
            _showErrorInputs.value = true
        }
    }

    private fun signUpUser(userSignIn: User) {
        viewModelScope.launch {
            _viewState.value = SignInViewState(isLoading = true)
            val accountCreated = createAccountUseCase(userSignIn)
            if (accountCreated) {
                _navigateToVerifyEmail.value = Event(true)
            } else {
                _showErrorDialog.value = true
            }
            _viewState.value = SignInViewState(isLoading = false)
        }
    }

    fun onLoginSelected() {
        _navigateToLogin.value = Event(true)
    }



    fun onFieldsChanged(userSignIn: User, passwordConfirmation: String?) {
        _viewState.value = userSignIn.toSignInViewState(passwordConfirmation)
    }

    private fun isValidOrEmptyEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()

    private fun isValidOrEmptyPassword(password: String, passwordConfirmation: String): Boolean =
        (PASSWORD_REGEX.matcher(password).matches() && password == passwordConfirmation) || password.isEmpty() || passwordConfirmation.isEmpty()

    private fun isValidName(name: String): Boolean =
        verifyUserName(name) || name.isEmpty()

    private fun User.toSignInViewState(passwordConfirmation: String?): SignInViewState {
        return SignInViewState(
            isValidEmail = isValidOrEmptyEmail(email),
            isValidFullName = isValidName(fullName),
            isValidDocument = isValidOrEmptyDocument(document),
            isValidProvince = isValidOrEmptyProvince(province),
            isValidMunicipality = isValidOrEmptyMunicipality(municipality),
            isValidAddress = isValidOrEmptyAddress(defaultAdress),
            isValidPhone = isValidOrEmptyPhone(phoneNumber),
            isValidPassword = isValidOrEmptyPassword(password, passwordConfirmation.toString()),
            )
    }
    private fun verifyUserName(
        fullName: String
    ): Boolean {
        for (badWord in BAD_WORDS)
        {
            if(fullName.lowercase().contains(badWord)){
                return false
            }
        }
        return true
    }
    private fun isValidOrEmptyDocument(document: String) =
        document.length == 8 || document.isEmpty()

    private fun isValidOrEmptyProvince(province: String) =
        province.isEmpty() || province.length >= MIN_SIGNUP_LENGTH

    private fun isValidOrEmptyMunicipality(municipality: String) =
        municipality.isEmpty() || municipality.length >= MIN_SIGNUP_LENGTH

    private fun isValidOrEmptyAddress(address: String) =
        address.isEmpty() || address.length >= MIN_SIGNUP_LENGTH

    private fun isValidOrEmptyPhone(phone: String) =
        phone.isNotEmpty()
}