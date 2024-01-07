package com.sokamn.trovami.ui.auth.login

import android.app.Activity
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.sokamn.trovami.core.Event
import com.sokamn.trovami.domain.model.UserLogin
import com.sokamn.trovami.domain.usecase.auth.EmailLoginUseCase
import com.sokamn.trovami.domain.usecase.auth.GoogleLoginUseCase
import com.sokamn.trovami.domain.usecase.user.GetUserModelByUidUseCase
import com.sokamn.trovami.utils.AuthConstants.CLIENT_ID
import com.sokamn.trovami.utils.AuthConstants.EMAIL
import com.sokamn.trovami.utils.AuthConstants.GOOGLE
import com.sokamn.trovami.utils.AuthConstants.LOGIN_ACTIVITY
import com.sokamn.trovami.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val emailLoginUseCase: EmailLoginUseCase,
    val googleLoginUseCase: GoogleLoginUseCase,
    val getUserModelByUidUseCase: GetUserModelByUidUseCase
) : ViewModel() {

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(CLIENT_ID)
            .requestEmail()
            .build()
    }

    private val _navigateToMain = MutableLiveData<Event<String>>()
    val navigateToMain: LiveData<Event<String>>
        get() = _navigateToMain

    private val _navigateToForgotPassword = MutableLiveData<Event<Boolean>>()
    val navigateToForgotPassword: LiveData<Event<Boolean>>
        get() = _navigateToForgotPassword

    private val _navigateToSignIn = MutableLiveData<Event<Array<String>>>()
    val navigateToSignIn: LiveData<Event<Array<String>>>
        get() = _navigateToSignIn

    private val _navigateToVerifyAccount = MutableLiveData<Event<String>>()
    val navigateToVerifyAccount: LiveData<Event<String>>
        get() = _navigateToVerifyAccount

    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState: StateFlow<LoginViewState>
        get() = _viewState

    private var _showErrorDialog = MutableLiveData(UserLogin())
    val showErrorDialog: LiveData<UserLogin>
        get() = _showErrorDialog

    private val _googleClient = MutableLiveData<GoogleSignInClient>()
    val googleClient: LiveData<GoogleSignInClient>
        get() = _googleClient

    fun onLoginSelected(email: String, password: String) {
        if (isValidEmail(email) && isValidPassword(password)) {
            loginUser(email, password)
        } else {
            onFieldsChanged(email, password)
        }
    }

    fun googleSignIn(account: GoogleSignInAccount){
        viewModelScope.launch {
            _viewState.value = LoginViewState(isLoading = true)
            when(val result = googleLoginUseCase(account)){
                is Resource.Error -> {
                    _showErrorDialog.value = UserLogin("","",true)
                }
                is Resource.Success -> {
                    if (result.data.isVerified) {
                        if (getUserModelByUidUseCase(result.data.userUID) != null) {
                            _navigateToMain.value = Event(result.data.userUID)
                        } else {
                            if (result.data.userUID == "AUTH ERROR") {
                                _showErrorDialog.value = UserLogin("", "", true)
                            } else {
                                _navigateToSignIn.value = Event(arrayOf(GOOGLE.toString(), LOGIN_ACTIVITY, account.displayName.toString(), account.email.toString()))
                            }
                        }
                    }
                }
            }
            _viewState.value = LoginViewState(isLoading = false)
        }
    }

    private fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            Log.e("soki","hola prro")
            _viewState.value = LoginViewState(isLoading = true)
            when (val result = emailLoginUseCase(email, password)) {
                is Resource.Error -> _showErrorDialog.value = UserLogin(email = email, password = password, showErrorDialog = true)
                is Resource.Success ->{
                    if (result.data.isVerified) {
                        _navigateToMain.value = Event(result.data.userUID)
                    } else {
                        _navigateToVerifyAccount.value = Event(result.data.userUID)
                    }
                }
            }
            _viewState.value = LoginViewState(isLoading = false)
        }
    }

    fun onFieldsChanged(email: String, password: String) {
        _viewState.value = LoginViewState(
            isValidEmail = isValidEmail(email),
            isValidPassword = isValidPassword(password)
        )
    }

    fun onForgotPasswordSelected() {
        _navigateToForgotPassword.value = Event(true)
    }

    fun onEmailSignInSelected(){
        _navigateToSignIn.value = Event(arrayOf(EMAIL.toString(), LOGIN_ACTIVITY,"",""))
    }

    fun onGoogleSignInSelected(activity: Activity){
        _googleClient.value = GoogleSignIn.getClient(activity, gso)
    }

    private fun isValidEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()

    private fun isValidPassword(password: String): Boolean =
        password.length >= MIN_PASSWORD_LENGTH || password.isEmpty()
}