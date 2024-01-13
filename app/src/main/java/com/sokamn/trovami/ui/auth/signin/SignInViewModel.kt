package com.sokamn.trovami.ui.auth.signin

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokamn.trovami.R
import com.sokamn.trovami.core.Event
import com.sokamn.trovami.domain.model.UserModel
import com.sokamn.trovami.domain.usecase.auth.CreateAccountUseCase
import com.sokamn.trovami.domain.usecase.auth.HasBeenEmailUsedUseCase
import com.sokamn.trovami.domain.usecase.user.CreateUserTableUseCase
import com.sokamn.trovami.utils.AuthConstants.EMAIL
import com.sokamn.trovami.utils.AuthConstants.GOOGLE
import com.sokamn.trovami.utils.AuthConstants.MIN_TEXT_CONTENT
import com.sokamn.trovami.utils.AuthConstants.PASSWORD_REGEX
import com.sokamn.trovami.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val createAccountUseCase: CreateAccountUseCase,
    private val createUserTableUseCase: CreateUserTableUseCase,
    private val hasBeenEmailUsedUseCase: HasBeenEmailUsedUseCase
) :
    ViewModel() {

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin

    private val _navigateToVerifyEmail = MutableLiveData<Event<String>>()
    val navigateToVerifyEmail: LiveData<Event<String>>
        get() = _navigateToVerifyEmail

    private val _navigateToMain = MutableLiveData<Event<String>>()
    val navigateToMain: LiveData<Event<String>>
        get() = _navigateToMain

    private val _viewState = MutableStateFlow(SignInViewState())
    val viewState: StateFlow<SignInViewState>
        get() = _viewState

    private var _showErrorDialog = MutableLiveData<Event<Int>>()
    val showErrorDialog: LiveData<Event<Int>>
        get() = _showErrorDialog

    private var _showErrorInputs = MutableLiveData(false)
    val showErrorInputs: LiveData<Boolean>
        get() = _showErrorInputs

    fun onGoogleSignInSelected(userSignIn: UserModel) {
        val viewState = userSignIn.toSignInViewState("", GOOGLE)
        signUpUser(userSignIn, GOOGLE)
    }

    fun onEmailSignInSelected(userSignIn: UserModel, passwordConfirmation: String) {
        val viewState = userSignIn.toSignInViewState(passwordConfirmation, EMAIL)
        if (viewState.userValidated() && userSignIn.isNotEmpty()) {
            signUpUser(userSignIn, EMAIL)
        } else {
            onFieldsChanged(userSignIn, passwordConfirmation, EMAIL)
            _showErrorInputs.value = true
        }
    }

    private fun signUpUser(userSignIn: UserModel, loginMethod: Int) { // REFACTORIZAR POR SEPARADO
        viewModelScope.launch {
            _viewState.value = SignInViewState(isLoading = true)
            when (val emailUsedResult = hasBeenEmailUsedUseCase(userSignIn.email)) {
                is Resource.Error -> _showErrorDialog.value =
                    Event(R.string.signin_network_error_description)

                is Resource.Success -> {
                    var emailExist = emailUsedResult.data
                    if (loginMethod == GOOGLE) emailExist = false
                    if (!emailExist) {
                        when (loginMethod) {
                            EMAIL -> {
                                when (val createAccountResult = createAccountUseCase(userSignIn)) {
                                    is Resource.Error -> _showErrorDialog.value =
                                        Event(R.string.signin_network_error_description)

                                    is Resource.Success -> {
                                        if (createAccountResult.data.isVerified) {
                                            _navigateToMain.value =
                                                Event(createAccountResult.data.userUID)
                                        } else {
                                            if (createAccountResult.data.userUID == "AUTH ERROR") {
                                                _showErrorDialog.value =
                                                    Event(R.string.signin_network_error_description)
                                            } else {
                                                _navigateToVerifyEmail.value =
                                                    Event(createAccountResult.data.userUID)
                                            }
                                        }
                                    }
                                }
                            }

                            GOOGLE -> {
                                when (val createUserTableResult =
                                    createUserTableUseCase(userSignIn)) {
                                    is Resource.Error -> _showErrorDialog.value =
                                        Event(R.string.signin_network_error_description)

                                    is Resource.Success -> {
                                        _navigateToMain.value = Event(createUserTableResult.data)
                                    }
                                }
                            }
                        }
                    } else {
                        _showErrorDialog.value =
                            Event(R.string.signin_email_has_been_used_error_description)
                        _viewState.value = SignInViewState(isValidEmail = false)
                    }
                }
            }
            _viewState.value = SignInViewState(isLoading = false)
        }
    }

    fun onLoginSelected() {
        _navigateToLogin.value = Event(true)
    }

    fun onFieldsChanged(userSignIn: UserModel, passwordConfirmation: String, loginMethod: Int) {
        _viewState.value = userSignIn.toSignInViewState(passwordConfirmation, loginMethod)
    }

    private fun UserModel.toSignInViewState(
        passwordConfirmation: String,
        loginMethod: Int
    ): SignInViewState {
        return if (loginMethod == GOOGLE) {
            SignInViewState(
                isValidEmail = isValidOrEmptyEmail(email),
                isValidFullName = isValidName(fullName),
                isValidDocument = isValidOrEmptyDocument(document),
                isValidProvince = isValidOrEmptyProvince(province),
                isValidMunicipality = isValidOrEmptyMunicipality(municipality),
                isValidAddress = isValidOrEmptyAddress(defaultAdress),
                isValidPhone = isValidOrEmptyPhone(phoneNumber),
                isValidPassword = true,
                isValidPasswordConfirmation = true
            )
        } else {
            SignInViewState(
                isValidEmail = isValidOrEmptyEmail(email),
                isValidFullName = isValidName(fullName),
                isValidDocument = isValidOrEmptyDocument(document),
                isValidProvince = isValidOrEmptyProvince(province),
                isValidMunicipality = isValidOrEmptyMunicipality(municipality),
                isValidAddress = isValidOrEmptyAddress(defaultAdress),
                isValidPhone = isValidOrEmptyPhone(phoneNumber),
                isValidPassword = isValidOrEmptyPassword(password),
                isValidPasswordConfirmation = isValidOrEmptyPasswordConfirmation(
                    password,
                    passwordConfirmation
                )
            )
        }
    }

    private fun isValidOrEmptyEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()

    private fun isValidOrEmptyPassword(password: String): Boolean =
        (PASSWORD_REGEX.matcher(password).matches()) || password.isEmpty()

    private fun isValidName(name: String): Boolean =
        name.length >= MIN_TEXT_CONTENT || name.isEmpty()

    private fun isValidOrEmptyPasswordConfirmation(
        password: String,
        passwordConfirmation: String
    ): Boolean =
        password == passwordConfirmation || passwordConfirmation.isEmpty()

    private fun isValidOrEmptyDocument(document: String) =
        document.length == 8 || document.isEmpty()

    private fun isValidOrEmptyProvince(province: String) =
        province.length >= MIN_TEXT_CONTENT || province.isEmpty()

    private fun isValidOrEmptyMunicipality(municipality: String) =
        municipality.length >= MIN_TEXT_CONTENT || municipality.isEmpty()

    private fun isValidOrEmptyAddress(address: String) =
        address.length >= MIN_TEXT_CONTENT || address.isEmpty()

    private fun isValidOrEmptyPhone(phone: String) =
        phone.length >= MIN_TEXT_CONTENT || phone.isEmpty()


}