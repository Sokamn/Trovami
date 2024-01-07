package com.sokamn.trovami.ui.auth.introduction

import android.app.Activity
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
import com.sokamn.trovami.domain.usecase.auth.GoogleLoginUseCase
import com.sokamn.trovami.domain.usecase.user.GetUserModelByUidUseCase
import com.sokamn.trovami.ui.auth.login.LoginViewState
import com.sokamn.trovami.utils.AuthConstants
import com.sokamn.trovami.utils.AuthConstants.EMAIL
import com.sokamn.trovami.utils.AuthConstants.GOOGLE
import com.sokamn.trovami.utils.AuthConstants.INTRODUCTION_ACTIVITY
import com.sokamn.trovami.utils.AuthConstants.LOGIN_ACTIVITY
import com.sokamn.trovami.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val getUserModelByUidUseCase: GetUserModelByUidUseCase
) : ViewModel() {

    private companion object {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(AuthConstants.CLIENT_ID)
            .requestEmail()
            .build()
    }

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin

    private val _navigateToSignIn = MutableLiveData<Event<Array<String>>>()
    val navigateToSignIn: LiveData<Event<Array<String>>>
        get() = _navigateToSignIn

    private val _navigateToMain = MutableLiveData<Event<String>>()
    val navigateToMain: LiveData<Event<String>>
        get() = _navigateToMain

    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState: StateFlow<LoginViewState>
        get() = _viewState

    private var _showErrorDialog = MutableLiveData(UserLogin())
    val showErrorDialog: LiveData<UserLogin>
        get() = _showErrorDialog

    private val _googleClient = MutableLiveData<GoogleSignInClient>()
    val googleClient: LiveData<GoogleSignInClient>
        get() = _googleClient

    fun googleSignIn(account: GoogleSignInAccount){
        viewModelScope.launch {
            _viewState.value = LoginViewState(isLoading = true)
            when(val result = googleLoginUseCase(account)){
                is Resource.Error -> { // ULTRA ERRRORRRRRRR NO DEBERIA SER ASÏ DEBERIA TENER UNO INDIVIDUAL: SHOW GOOGLE ERROR DIALOG
                    _showErrorDialog.value = UserLogin("","",true)
                }
                is Resource.Success -> {
                    if (result.data.isVerified) {
                        if (getUserModelByUidUseCase(result.data.userUID) != null) {
                            _navigateToMain.value = Event(result.data.userUID)
                        } else {
                            if (result.data.userUID == "AUTH ERROR") {// ULTRA ERRRORRRRRRR NO DEBERIA SER ASÏ DEBERIA TENER UNO INDIVIDUAL: SHOW GOOGLE ERROR DIALOG
                                _showErrorDialog.value = UserLogin("", "", true)
                                // TENER CUIDADO QUE HAY Q ARREGLAR EN LOGIN TAMBIEN
                            } else {
                                _navigateToSignIn.value = Event(arrayOf(
                                    GOOGLE.toString(),
                                    LOGIN_ACTIVITY,
                                    account.displayName.toString(),
                                    account.email.toString()))
                            }
                        }
                    }
                }
            }
            _viewState.value = LoginViewState(isLoading = false)
        }
    }

    fun onGoogleSignInSelected(activity: Activity){
        _googleClient.value = GoogleSignIn.getClient(activity, gso)
    }

    fun onLoginSelected() {
        _navigateToLogin.value = Event(true)
    }

    fun onEmailSignInSelected() {
        _navigateToSignIn.value = Event(arrayOf(EMAIL.toString(), INTRODUCTION_ACTIVITY, "", ""))
    }

}