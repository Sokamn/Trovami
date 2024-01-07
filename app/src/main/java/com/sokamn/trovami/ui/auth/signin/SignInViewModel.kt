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
import com.sokamn.trovami.domain.usecase.auth.CreateUserTableUseCase
import com.sokamn.trovami.domain.usecase.auth.HasBeenEmailUsedUseCase
import com.sokamn.trovami.utils.AppConstants
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

    private val _navigateToVerifyEmail = MutableLiveData<Event<Boolean>>()
    val navigateToVerifyEmail: LiveData<Event<Boolean>>
        get() = _navigateToVerifyEmail

    private val _navigateToMain = MutableLiveData<Event<Boolean>>()
    val navigateToMain: LiveData<Event<Boolean>>
        get() = _navigateToMain

    private val _currentUserUid = MutableLiveData<String>()
    val currentUserUid: LiveData<String>
        get() = _currentUserUid

    private val _postAccountState: MutableLiveData<Resource<Unit>> = MutableLiveData()
    val postAccountState: LiveData<Resource<Unit>>
        get() = _postAccountState

    private val _viewState = MutableStateFlow(SignInViewState())
    val viewState: StateFlow<SignInViewState>
        get() = _viewState

    private var _showErrorDialog = MutableLiveData<Event<Int>>()
    val showErrorDialog: LiveData<Event<Int>>
        get() = _showErrorDialog

    private var _showErrorInputs = MutableLiveData(false)
    val showErrorInputs: LiveData<Boolean>
        get() = _showErrorInputs

    fun onSignUpSelected(userSignIn: UserModel, passwordConfirmation: String?, loginMethod: Int) {
        if (loginMethod == AppConstants.GOOGLE){
            signUpUser(userSignIn,loginMethod)
        }
        else{
            val viewState = userSignIn.toSignInViewState(passwordConfirmation)
            if (viewState.userValidated() && userSignIn.isNotEmpty()) {
                signUpUser(userSignIn,loginMethod)
            } else {
                onFieldsChanged(userSignIn, passwordConfirmation)
                _showErrorInputs.value = true
            }
        }
    }

    private fun signUpUser(userSignIn: UserModel,loginMethod: Int) {
        viewModelScope.launch {
            _viewState.value = SignInViewState(isLoading = true)
            when(val emailUsedResult = hasBeenEmailUsedUseCase(userSignIn.email)){
                is Resource.Error -> _showErrorDialog.value = Event(R.string.signin_network_error_description)
                is Resource.Success ->{
                    var emailExist = emailUsedResult.data
                    if (loginMethod == AppConstants.GOOGLE) emailExist = false
                    if (!emailExist){
                        when(loginMethod){
                            AppConstants.EMAIL ->{
                                when(val createAccountResult = createAccountUseCase(userSignIn)){
                                    is Resource.Error -> _showErrorDialog.value = Event(R.string.signin_network_error_description)
                                    is Resource.Success -> {
                                        _currentUserUid.value = createAccountResult.data.userUID
                                        if (createAccountResult.data.isVerified) {
                                            _navigateToMain.value = Event(true)
                                        }else{
                                            _navigateToVerifyEmail.value = Event(true)
                                        }
                                    }
                                }
                            }
                            AppConstants.GOOGLE ->{
                                when(val createUserTableResult = createUserTableUseCase(userSignIn)){
                                    is Resource.Error -> _showErrorDialog.value = Event(R.string.signin_network_error_description)
                                    is Resource.Success -> {
                                        _currentUserUid.value = createUserTableResult.data

                                    }
                                }
                            }
                        }
                    }else{
                        _showErrorEmailExists.value = true
                        _viewState.value  = SignInViewState(isValidEmail = false)
                    }
                }
            }


            _viewState.value = SignInViewState(isLoading = false)
        }
    }

    fun onLoginSelected() {
        _navigateToLogin.value = Event(true)
    }

    fun onFieldsChanged(userSignIn: UserModel, passwordConfirmation: String?) {
        _viewState.value = userSignIn.toSignInViewState(passwordConfirmation)
    }

    private fun isValidOrEmptyEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()

    private fun isValidOrEmptyPassword(password: String, passwordConfirmation: String): Boolean =
        (AppConstants.PASSWORD_REGEX.matcher(password).matches() && password == passwordConfirmation) || password.isEmpty() || passwordConfirmation.isEmpty()

    private fun isValidName(name: String): Boolean =
        name.isEmpty()

    private fun UserModel.toSignInViewState(passwordConfirmation: String?): SignInViewState {
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